package com.springboot.gotgam.infrastructure.persistence.elasticsearch.repository;

import com.springboot.gotgam.infrastructure.persistence.elasticsearch.document.DiaryDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 다이어리 Elasticsearch 리포지토리
 */
public interface ElasticsearchDiaryRepository extends ElasticsearchRepository<DiaryDocument, String> {
    
    /**
     * 멤버 ID로 다이어리 조회
     */
    List<DiaryDocument> findByMemberId(Long memberId);
    
    /**
     * 멤버 ID와 공개 여부로 다이어리 조회
     */
    Page<DiaryDocument> findByMemberIdAndIsPublic(Long memberId, boolean isPublic, Pageable pageable);
    
    /**
     * 태그로 다이어리 조회
     */
    @Query("{\"bool\": {\"must\": [{\"terms\": {\"tags\": ?0}}, {\"term\": {\"is_public\": true}}]}}")
    Page<DiaryDocument> findByTags(List<String> tags, Pageable pageable);
    
    /**
     * 제목, 내용, 지역에 키워드가 포함된 다이어리 조회
     */
    @Query("{\"bool\": {\"must\": [{\"multi_match\": {\"query\": ?0, \"fields\": [\"title\", \"content\", \"region\"]}}, {\"term\": {\"is_public\": true}}]}}")
    Page<DiaryDocument> searchByKeyword(String keyword, Pageable pageable);
    
    /**
     * 지역 코드로 다이어리 조회
     */
    Page<DiaryDocument> findByAreaCodeAndSigunguCodeAndIsPublic(String areaCode, String sigunguCode, boolean isPublic, Pageable pageable);
    
    /**
     * 가격 범위로 다이어리 조회
     */
    @Query("{\"bool\": {\"must\": [{\"range\": {\"total_cost\": {\"gte\": ?0, \"lte\": ?1}}}, {\"term\": {\"is_public\": true}}]}}")
    Page<DiaryDocument> findByPriceRange(int minPrice, int maxPrice, Pageable pageable);
}
