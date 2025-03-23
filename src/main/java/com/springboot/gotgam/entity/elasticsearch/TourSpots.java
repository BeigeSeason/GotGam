package com.springboot.gotgam.entity.elasticsearch;

import com.springboot.gotgam.dto.tourspot.TourSpotListDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(indexName = "tour_spots")
public class TourSpots {
    // 엘라스틱 서치 자체 아이디
    @Id
    private String id;

    // 관광지 아이디
    @Field(name = "content_id", type = FieldType.Keyword)
    private String contentId;

    // 주소
    // N-gram 분석은 텍스트를 N-gram (N글자씩 묶음) 단위로 분리하여 인덱싱하는 방식
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer_simple"),
            otherFields = {
                    @InnerField(type = FieldType.Text, analyzer = "nori_ngram_analyzer", suffix = "ngram")
            }
    )
    private String addr1;

    // 상세주소
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer_simple"),
            otherFields = {
                    @InnerField(type = FieldType.Text, analyzer = "nori_ngram_analyzer", suffix = "ngram")
            }
    )
    private String addr2;

    // 제목
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "nori_analyzer_simple"),
            otherFields = {
                    @InnerField(type = FieldType.Text, analyzer = "nori_ngram_analyzer", suffix = "ngram")
            }
    )
    private String title;

    // 지역코드(시도)
    @Field(type = FieldType.Keyword, name = "area_code")
    private String areaCode;

    // 지역코드(시군구)
    @Field(type = FieldType.Keyword, name = "sigungu_code")
    private String sigunguCode;

    // 타입 아이디
    @Field(type = FieldType.Keyword, name = "content_type_id")
    private String contentTypeId;

    // 분류한 타입 아이디
    @Field(type = FieldType.Keyword, name = "classified_type_id")
    private String classifiedTypeId;

    // 대분류
    @Field(type = FieldType.Keyword)
    private String cat1;

    // 중분류
    @Field(type = FieldType.Keyword)
    private String cat2;

    // 소분류
    @Field(type = FieldType.Keyword)
    private String cat3;

    // 등록일
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, name = "created_time")
    private LocalDateTime createdTime;

    // 수정일
    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second, name = "modified_time")
    private LocalDateTime modifiedTime;

    // 이미지 1
    @Field(type = FieldType.Keyword, name = "first_image")
    private String firstImage;

    // 이미지 2
    @Field(type = FieldType.Keyword, name = "first_image2")
    private String firstImage2;

    // 전화번호
    @Field(type = FieldType.Keyword, name = "tel")
    private String tel;

    // 경도
    @Field(type = FieldType.Float, name = "map_x")
    private Float mapX;

    // 위도
    @Field(type = FieldType.Float, name = "map_y")
    private Float mapY;

    // 좌표
    @GeoPointField
    private GeoPoint location; // geo_point 타입 필드

    // 중첩 객체 관리를 위해 Nested 설정
    @Field(type = FieldType.Nested)
    private Detail detail;

    // 리뷰 갯수
    @Field(type = FieldType.Float, name = "review_count")
    private int reviewCount;

    // 총 점수
    @Field(type = FieldType.Float, name = "rating")
    private float rating;

    // 평균 점수
    @Field(type = FieldType.Double, name = "avg_rating")
    private double avgRating;

    // 북마크 갯수
    @Field(type = FieldType.Float, name = "bookmark_count")
    private int bookmarkCount;

    // detail 필드는 여기서밖에 사용하지 않기 때문에 엔티티 내부에 정의함
    @Data
    public static class Detail {
        // 이미지 목록
        @Field(type = FieldType.Keyword)
        private List<String> images;

        // 문의(연락처)
        @Field(type = FieldType.Keyword, name = "info_center")
        private String infoCenter;

        // 설명
        @Field(type = FieldType.Keyword)
        private String overview;

        // 이용시간
        @Field(type = FieldType.Keyword, name = "use_time")
        private String useTime;

        // 주차
        @Field(type = FieldType.Keyword)
        private String parking;

        // 홈페이지
        @Field(type = FieldType.Keyword)
        private String homepage;
    }

    public TourSpotListDto convertToListDto() {
        return TourSpotListDto.builder()
                .spotId(contentId)
                .title(title)
                .addr(addr1)
                .thumbnail(firstImage)
                .cat1(cat1)
                .cat2(cat2)
                .cat3(cat3)
                .reviewCount(reviewCount)
                .avgRating(avgRating)
                .bookmarkCount(bookmarkCount)
                .build();
    }

    public TourSpotListDto convertToSimpleDto() {
        return TourSpotListDto.builder()
                .spotId(contentId)
                .title(title)
                .thumbnail(firstImage)
                .build();
    }
}