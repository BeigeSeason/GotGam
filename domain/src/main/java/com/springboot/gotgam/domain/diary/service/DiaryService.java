package com.springboot.gotgam.domain.diary.service;

import com.springboot.gotgam.domain.diary.model.Diary;
import com.springboot.gotgam.domain.member.model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * 다이어리 도메인 서비스 인터페이스
 */
public interface DiaryService {
    
    /**
     * 다이어리 생성
     */
    Diary createDiary(String title, String content, boolean isPublic, LocalDate startDate, LocalDate endDate,
                      String region, String areaCode, String sigunguCode, int totalCost, List<String> tags, Member author);
    
    /**
     * 다이어리 조회
     */
    Diary getDiaryById(String id);
    
    /**
     * 다이어리 수정
     */
    Diary updateDiary(String id, String title, String content, boolean isPublic, LocalDate startDate, LocalDate endDate,
                      String region, String areaCode, String sigunguCode, int totalCost, List<String> tags, Member currentUser);
    
    /**
     * 다이어리 삭제
     */
    void deleteDiary(String id, Member currentUser);
    
    /**
     * 내 다이어리 목록 조회
     */
    Page<Diary> getMyDiaries(Member member, Pageable pageable);
    
    /**
     * 특정 사용자의 공개 다이어리 목록 조회
     */
    Page<Diary> getUserPublicDiaries(Member author, Pageable pageable);
    
    /**
     * 다이어리 검색
     */
    Page<Diary> searchDiaries(String keyword, String areaCode, String sigunguCode, 
                             Integer minPrice, Integer maxPrice, List<String> tags, Pageable pageable);
}
