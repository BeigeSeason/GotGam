package com.springboot.gotgam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.gotgam.dto.tourspot.TourSpotDetailDto;
import com.springboot.gotgam.dto.tourspot.TourSpotListDto;
import com.springboot.gotgam.dto.tourspot.TourSpotStats;
import com.springboot.gotgam.entity.elasticsearch.TourSpots;
import com.springboot.gotgam.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TourSpotService {
    private final TourSpotsRepository tourSpotsRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTemplate<String, TourSpotDetailDto> tourSpotDetailRedisTemplate; // 타입 변경
    private final ReviewRepository reviewRepository;
    private final BookmarkRepository bookmarkRepository;

    @Value("${tour.api.service-key1}")
    private String serviceKey1;

    @Value("${tour.api.service-key2}")
    private String serviceKey2;

    @Value("${tour.api.service-key3}")
    private String serviceKey3;

    private static final String BASE_URL = "https://apis.data.go.kr/B551011/KorService1";
    private static final String INDEX_NAME = "tour_spots";

    // contentTypeId에 따른 infoCenter 접미사 매핑 (상수)
    private static final Map<String, String> INFO_CENTER_SUFFIX;

    static {
        INFO_CENTER_SUFFIX = Map.of(
                "39", "food",
                "38", "shopping",
                "32", "lodging",
                "14", "culture"); // 불변 Map으로 설정
    }

    // 초기 호출
    public TourSpotDetailDto getTourSpotDetail(String tourSpotId) {
        return getTourSpotDetail(tourSpotId, 0);
    }

    /*public TourSpotDetailDto getTourSpotDetail(String tourSpotId, int retryCount) {
        if (retryCount > 8) throw new RuntimeException("재시도 횟수 초과");
        String lockKey = "lock:tourspot:" + tourSpotId; // 고유 락 키
        if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 10, TimeUnit.SECONDS))) {
            try {
                Optional<TourSpots> tourSpotOpt = tourSpotsRepository.findByContentId(tourSpotId);
                if (tourSpotOpt.isPresent()) {
                    TourSpots tourSpot = tourSpotOpt.get();
                    TourSpots.Detail detail = tourSpot.getDetail();
                    if (detail != null) {
                        if (!tourSpot.getFirstImage().isEmpty()) detail.getImages().add(0, tourSpot.getFirstImage());
                        return convertToDto(tourSpot, detail);
                    }
                    TourSpotDetailDto detailDto = fetchDetailFromApi(tourSpotId, tourSpot.getContentTypeId());
                    saveDetailToElasticsearch(tourSpot.getId(), detailDto);
                    if (!tourSpot.getFirstImage().isEmpty()) detailDto.getImages().add(0, tourSpot.getFirstImage());
                    detailDto.setAddr1(tourSpot.getAddr1());
                    detailDto.setMapX(tourSpot.getMapX());
                    detailDto.setMapY(tourSpot.getMapY());
                    detailDto.setNearSpots(findNearestTourSpots(tourSpot.getLocation(), tourSpot.getContentId()));
                    return detailDto;
                } else {
                    throw new RuntimeException("해당 관광지 데이터가 없습니다: " + tourSpotId);
                }
            } catch (Exception e) {
                log.error("상세 정보 조회 중 오류: {}", e.getMessage());
                throw new RuntimeException("상세 정보를 가져오지 못했습니다.");
            } finally {
                redisTemplate.delete(lockKey); // 락 해제
            }
        } else {
            // 락을 획득하지 못한 경우 대기 후 재시도
            try {
                Thread.sleep(150); // 150ms 대기
                return getTourSpotDetail(tourSpotId, retryCount + 1); // 재귀 호출
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("대기 중 인터럽트 발생", e);
            }
        }
    }*/

   public TourSpotDetailDto getTourSpotDetail(String tourSpotId, int retryCount) {
        long startTime = System.currentTimeMillis();
        String cacheKey = "tourspot:detail:" + tourSpotId; // 캐시 키

        // 1. 캐시 확인 (락 없이)
        TourSpotDetailDto cached = tourSpotDetailRedisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            long endTime = System.currentTimeMillis();
            log.info("캐시 히트: {} ms", endTime - startTime);
            return cached;
        }

        // 2. Elasticsearch 확인
        Optional<TourSpots> tourSpotOpt = tourSpotsRepository.findByContentId(tourSpotId);
        if (tourSpotOpt.isEmpty()) {
            throw new RuntimeException("해당 관광지 데이터가 없습니다: " + tourSpotId);
        }

        TourSpots tourSpot = tourSpotOpt.get();
        TourSpots.Detail detail = tourSpot.getDetail();
        // 최초 조회 시 중복 Api 호출을 막기 위한 캐싱이기 때문에 일반 조회는 캐싱하지 않는다.
        if (detail != null) {
            TourSpotDetailDto result = convertToDto(tourSpot, detail);
            if (!tourSpot.getFirstImage().isEmpty()) result.getImages().add(0, tourSpot.getFirstImage());
            long endTime = System.currentTimeMillis();
            log.info("이미 존재함: {} ms", endTime - startTime);
            return result;
        }

        if (retryCount > 10) {
            throw new RuntimeException("재시도 횟수 초과");
        }

        // 3. API 호출 필요: 락 적용
        String lockKey = "lock:tourspot:" + tourSpotId;
        if (Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(lockKey, "locked", 10, TimeUnit.SECONDS))) {
            try {
                // 락 내에서 캐시 재확인
                TourSpotDetailDto recheckCached = tourSpotDetailRedisTemplate.opsForValue().get(cacheKey);
                if (recheckCached != null) {
                    long endTime = System.currentTimeMillis();
                    log.info("락 내에서 캐시 재확인: {} ms", endTime - startTime);
                    return recheckCached;
                }

                // API 호출 및 저장
                TourSpotDetailDto detailDto = fetchDetailFromApi(tourSpotId, tourSpot.getContentTypeId());
                saveDetailToElasticsearch(tourSpot.getId(), detailDto);
                if (!tourSpot.getFirstImage().isEmpty()) detailDto.getImages().add(0, tourSpot.getFirstImage());
                detailDto.setAddr1(tourSpot.getAddr1());
                detailDto.setMapX(tourSpot.getMapX());
                detailDto.setMapY(tourSpot.getMapY());
                detailDto.setNearSpots(findNearestTourSpots(tourSpot.getLocation(), tourSpot.getContentId()));
                log.info("Caching TourSpotDetailDto with nearSpots: {}", detailDto.getNearSpots().toString());
                // Redis 캐시 저장
                tourSpotDetailRedisTemplate.opsForValue().set(cacheKey, detailDto, 1, TimeUnit.MINUTES);
                long endTime = System.currentTimeMillis();
                log.info("API 호출 후 반환 : {} ms", endTime - startTime);
                return detailDto;
            } catch (Exception e) {
                throw new RuntimeException("상세 정보를 가져오지 못했습니다.");
            } finally {
                redisTemplate.delete(lockKey);
            }
        } else {
            try {
                Thread.sleep(150);
                return getTourSpotDetail(tourSpotId, retryCount + 1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("대기 중 인터럽트 발생", e);
            }
        }
    }

    // 여행지 상세정보 존재하지 않을 시 Api 요청
    private TourSpotDetailDto fetchDetailFromApi(String contentId, String contentTypeId) {
        try {
            // 1. detailCommon1 호출
            String commonUrl = BASE_URL + "/detailCommon1?MobileOS=ETC&MobileApp=Final_test&_type=json" +
                    "&contentId=" + contentId + "&defaultYN=Y&overviewYN=Y&serviceKey=" + serviceKey1;
            Map<String, Object> commonItem = fetchApiData(commonUrl, "common", true);

            // 2. detailImage1 호출
            String imageUrl = BASE_URL + "/detailImage1?MobileOS=ETC&MobileApp=Final_test&_type=json" +
                    "&contentId=" + contentId + "&subImageYN=Y&serviceKey=" + serviceKey2;
            List<Map<String, Object>> imageItems = fetchApiData(imageUrl, "image", false);

            // 3. detailIntro1 호출
            String introUrl = BASE_URL + "/detailIntro1?MobileOS=ETC&MobileApp=Final_test&_type=json" +
                    "&contentId=" + contentId + "&contentTypeId=" + contentTypeId + "&serviceKey=" + serviceKey3;
            Map<String, Object> introItem = fetchApiData(introUrl, "intro", true);

            String addStr = INFO_CENTER_SUFFIX.getOrDefault(contentTypeId, "");
            String useTime = contentTypeId.equals("39") ? "opentimefood" : "usetime" + addStr;

            return TourSpotDetailDto.builder()
                    .contentId(contentId)
                    .title((String) commonItem.getOrDefault("title", ""))
                    .images(imageItems.stream()
                            .map(item -> (String) item.get("originimgurl"))
                            .collect(Collectors.toList()))
                    .overview((String) commonItem.getOrDefault("overview", ""))
                    .homepage((String) commonItem.getOrDefault("homepage", ""))
                    .infoCenter((String) introItem.getOrDefault("infocenter" + addStr, ""))
                    .useTime((String) introItem.getOrDefault(useTime, ""))
                    .parking((String) introItem.getOrDefault("parking" + addStr, ""))
                    .build();
        } catch (Exception e) {
            log.error("API 호출 중 오류 - contentId: {}: {}", contentId, e.getMessage());
            throw new RuntimeException("API 데이터를 가져오지 못했습니다.");
        }
    }

    // 공통 API 호출 메서드
    @SuppressWarnings("unchecked") // JSON 응답 구조가 Map<String, Object>로 고정돼 있어 안전함
    private <T> T fetchApiData(String url, String logLabel, boolean isSingleItem) {
        try {
            log.info("호출 URL ({}): {}", logLabel, url);

            URL urlObj = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/133.0.0.0 Safari/537.36");

            StringBuilder responseBody = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    responseBody.append(line);
                }
            }
            log.info("응답 상태 ({}): {}", logLabel, conn.getResponseCode());

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> response = mapper.readValue(responseBody.toString(), new TypeReference<Map<String, Object>>() {
            });

            return isSingleItem ? (T) extractItem(response) : (T) extractItems(response);
        } catch (Exception e) {
            log.error("API 호출 중 오류 ({}): {}", logLabel, e.getMessage());
            throw new RuntimeException("API 호출 실패: " + logLabel);
        }
    }

    // 상세정보 엘라스틱 서치에 저장하기
    private void saveDetailToElasticsearch(String spotId, TourSpotDetailDto detailDto) {
        UpdateQuery updateQuery = UpdateQuery.builder(spotId)
                .withDocument(Document.from(Map.of(
                        "detail", Map.of(
                                "images", detailDto.getImages(),
                                "overview", detailDto.getOverview(),
                                "info_center", detailDto.getInfoCenter(),
                                "homepage", detailDto.getHomepage(),
                                "use_time", detailDto.getUseTime(),
                                "parking", detailDto.getParking()
                        )
                )))
                .build();
        elasticsearchOperations.update(updateQuery, IndexCoordinates.of(INDEX_NAME));
        log.info("관광지 {} 상세 정보 저장 완료", spotId);
    }

    @SuppressWarnings("unchecked") // JSON 응답 구조가 Map<String, Object>로 고정돼 있어 안전함
    private Map<String, Object> extractItem(Map<String, Object> response) {
        try {
            Map<String, Object> body = (Map<String, Object>) response.get("response");
            Map<String, Object> items = (Map<String, Object>) ((Map<String, Object>) body.get("body")).get("items");
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) items.get("item");
            return itemList.get(0);
        } catch (Exception e) {
            log.error("아이템 추출 실패: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    @SuppressWarnings("unchecked") // JSON 응답 구조가 Map<String, Object>로 고정돼 있어 안전함
    private List<Map<String, Object>> extractItems(Map<String, Object> response) {
        try {
            Map<String, Object> body = (Map<String, Object>) response.get("response");
            Map<String, Object> items = (Map<String, Object>) ((Map<String, Object>) body.get("body")).get("items");
            return (List<Map<String, Object>>) items.get("item");
        } catch (Exception e) {
            log.error("이미지 목록 추출 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

 /*   // 캐싱(레디스의 잔재)
    public void cacheTourSpotStats(String tourSpotId) {
        TourSpotStats stats = fetchStatsFromMySQL(tourSpotId);
        String cacheKey = TourConstants.TOUR_SPOT_STATS_PREFIX + tourSpotId;
        redisTemplate.opsForValue().set(cacheKey, stats, 1, TimeUnit.HOURS);
    }*/

    // 단일 관광지의 리뷰/북마크 통계를 MySQL에서 조회해 TourSpotStats로 반환.
    private TourSpotStats fetchStatsFromMySQL(String tourSpotId) {
        Integer reviewCount = reviewRepository.countByTourSpotId(tourSpotId);
        Double avgRating = reviewRepository.avgRatingByTourSpotId(tourSpotId);
        Integer bookmarkCount = bookmarkRepository.countByBookmarkedId(tourSpotId);
        return new TourSpotStats(tourSpotId, reviewCount != null ? reviewCount : 0,
                avgRating != null ? avgRating : 0.0, bookmarkCount != null ? bookmarkCount : 0);
    }

    // GeoPoint로 가까운 TourSpots 10개 가져오기
    public List<TourSpotListDto> findNearestTourSpots(GeoPoint point, String exceptId) {
        Pageable pageable = PageRequest.of(0, 10);

        // GeoDistance 정렬 설정
        GeoDistanceSortBuilder geoSort = SortBuilders.geoDistanceSort("location", point.getLat(), point.getLon())
                .order(SortOrder.ASC)
                .unit(DistanceUnit.KILOMETERS);

        // 검색 범위를 50km로 제한하고 기준 장소 제외
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery()
                .filter(QueryBuilders.geoDistanceQuery("location")
                        .point(point.getLat(), point.getLon())
                        .distance(50, DistanceUnit.KILOMETERS)) // 50km 내 문서만 대상
                .mustNot(QueryBuilders.termQuery("content_id", exceptId)); // 기준 장소 제외

        Query query = new NativeSearchQueryBuilder()
                .withQuery(boolQuery)
                .withSorts(geoSort)
                .withPageable(pageable)
                .build();

        SearchHits<TourSpots> searchHits = elasticsearchOperations.search(query, TourSpots.class);
        if (searchHits.isEmpty()) {
            log.debug("No tour spots found near lat: {}, lon: {}", point.getLat(), point.getLon());
            return Collections.emptyList();
        }

        return searchHits.getSearchHits().stream()
                .map(this::mapToMinimalDto)
                .collect(Collectors.toList());
    }

    private TourSpotDetailDto convertToDto(TourSpots tourSpot, TourSpots.Detail detail) {
        return TourSpotDetailDto.builder()
                .contentId(tourSpot.getContentId())
                .title(tourSpot.getTitle())
                .addr1(tourSpot.getAddr1())
                .images(detail.getImages())
                .overview(detail.getOverview())
                .homepage(detail.getHomepage())
                .infoCenter(detail.getInfoCenter())
                .useTime(detail.getUseTime())
                .parking(detail.getParking())
                .mapX(tourSpot.getMapX())
                .mapY(tourSpot.getMapY())
                .nearSpots(findNearestTourSpots(tourSpot.getLocation(), tourSpot.getContentId()))
                .bookmarkCount(tourSpot.getBookmarkCount())
                .build();
    }

    // TourSpots를 최소 필드만 포함한 TourSpotListDto로 변환
    private TourSpotListDto mapToMinimalDto(SearchHit<TourSpots> hit) {
        TourSpots tourSpot = hit.getContent();

        return TourSpotListDto.builder()
                .spotId(tourSpot.getContentId())    // TourSpots의 spotId 필드
                .title(tourSpot.getTitle())      // TourSpots의 title 필드
                .thumbnail(tourSpot.getFirstImage()) // TourSpots의 thumbnail 필드
                .build();
    }
}