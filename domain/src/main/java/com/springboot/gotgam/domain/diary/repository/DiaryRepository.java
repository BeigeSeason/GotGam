package com.springboot.gotgam.domain.diary.repository;

import com.springboot.gotgam.domain.diary.model.Diary;
import com.springboot.gotgam.domain.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * 다이어리 리포지토리 인터페이스
 * 이 인터페이스는 도메인 계층에 위치하며, 구현체는 인프라 계층에 위치합니다.
 */
public interface DiaryRepository {
    
    /**
     * ID로 다이어리 조회
     */
    Optional<Diary> findById(String id);
    
    /**
     * 다이어리 저장
     */
    Diary save(Diary diary);
    
    /**
     * 다이어리 삭제
     */
    void delete(Diary diary);
    
    /**
     * 특정 작성자의 모든 다이어리 조회
     */
    List<Diary> findAllByAuthor(Member author);
    
    /**
     * 특정 작성자의 공개 다이어리 조회
     */
    List<Diary> findAllByAuthorAndIsPublic(Member author, boolean isPublic);
    
    /**
     * 키워드로 다이어리 검색
     */
    Page<Diary> search(String keyword, Pageable pageable);
    
    /**
     * 태그로 다이어리 검색
     */
    Page<Diary> searchByTags(List<String> tags, Pageable pageable);
    
    /**
     * 지역 코드로 다이어리 검색
     */
    Page<Diary> searchByAreaCodeAndSigunguCode(String areaCode, String sigunguCode, Pageable pageable);
    
    /**
     * 가격 범위로 다이어리 검색
     */
    Page<Diary> searchByPriceRange(int minPrice, int maxPrice, Pageable pageable);
}
