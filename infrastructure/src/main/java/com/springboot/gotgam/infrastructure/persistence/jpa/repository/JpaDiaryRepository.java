package com.springboot.gotgam.infrastructure.persistence.jpa.repository;

import com.springboot.gotgam.infrastructure.persistence.jpa.entity.DiaryEntity;
import com.springboot.gotgam.infrastructure.persistence.jpa.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * 다이어리 JPA 리포지토리
 */
public interface JpaDiaryRepository extends JpaRepository<DiaryEntity, String> {
    
    /**
     * 특정 회원의 모든 다이어리 조회
     */
    List<DiaryEntity> findAllByMember(MemberEntity member);
    
    /**
     * 특정 회원의 공개/비공개 다이어리 조회
     */
    List<DiaryEntity> findAllByMemberAndIsPublic(MemberEntity member, boolean isPublic);
    
    /**
     * 특정 지역의 다이어리 조회
     */
    Page<DiaryEntity> findAllByAreaCodeAndSigunguCode(String areaCode, String sigunguCode, Pageable pageable);
    
    /**
     * 가격 범위로 다이어리 조회
     */
    @Query("SELECT d FROM DiaryEntity d WHERE d.totalCost BETWEEN :minPrice AND :maxPrice")
    Page<DiaryEntity> findByPriceRange(@Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice, Pageable pageable);
    
    /**
     * 공개된 다이어리 중 제목이나 내용에 키워드가 포함된 다이어리 조회
     */
    @Query("SELECT d FROM DiaryEntity d WHERE (d.title LIKE %:keyword% OR d.content LIKE %:keyword%) AND d.isPublic = true")
    Page<DiaryEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 태그로 다이어리 검색
     * JPQL에서 컬렉션 내 원소 검색을 위한 MEMBER OF 사용
     */
    @Query("SELECT d FROM DiaryEntity d JOIN d.tags t WHERE t IN :tags AND d.isPublic = true")
    Page<DiaryEntity> findByTagsContaining(@Param("tags") List<String> tags, Pageable pageable);
}
