package com.springboot.gotgam.domain.diary.service.impl;

import com.springboot.gotgam.core.exception.BaseException;
import com.springboot.gotgam.domain.diary.model.Diary;
import com.springboot.gotgam.domain.diary.repository.DiaryRepository;
import com.springboot.gotgam.domain.diary.service.DiaryService;
import com.springboot.gotgam.domain.member.model.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 다이어리 도메인 서비스 구현
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {
    
    private final DiaryRepository diaryRepository;
    
    @Override
    @Transactional
    public Diary createDiary(String title, String content, boolean isPublic, LocalDate startDate, LocalDate endDate,
                           String region, String areaCode, String sigunguCode, int totalCost, List<String> tags, Member author) {
        // 다이어리 도메인 객체 생성
        Diary diary = Diary.builder()
                .id(UUID.randomUUID().toString())
                .title(title)
                .content(content)
                .isPublic(isPublic)
                .startDate(startDate)
                .endDate(endDate)
                .region(region)
                .areaCode(areaCode)
                .sigunguCode(sigunguCode)
                .totalCost(totalCost)
                .tags(tags)
                .author(author)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        // 저장 및 결과 반환
        return diaryRepository.save(diary);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Diary getDiaryById(String id) {
        return diaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("다이어리를 찾을 수 없습니다. ID: " + id));
    }
    
    @Override
    @Transactional
    public Diary updateDiary(String id, String title, String content, boolean isPublic, LocalDate startDate, LocalDate endDate,
                           String region, String areaCode, String sigunguCode, int totalCost, List<String> tags, Member currentUser) {
        // 기존 다이어리 조회
        Diary diary = getDiaryById(id);
        
        // 권한 검증
        if (!diary.isOwnedBy(currentUser)) {
            throw new RuntimeException("다이어리를 수정할 권한이 없습니다.");
        }
        
        // 내용 업데이트
        diary.update(title, content, startDate, endDate, region, areaCode, sigunguCode, totalCost, tags);
        diary.changeVisibility(isPublic);
        
        // 저장 및 결과 반환
        return diaryRepository.save(diary);
    }
    
    @Override
    @Transactional
    public void deleteDiary(String id, Member currentUser) {
        // 기존 다이어리 조회
        Diary diary = getDiaryById(id);
        
        // 권한 검증
        if (!diary.isOwnedBy(currentUser)) {
            throw new RuntimeException("다이어리를 삭제할 권한이 없습니다.");
        }
        
        // 삭제
        diaryRepository.delete(diary);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Diary> getMyDiaries(Member member, Pageable pageable) {
        // 내 다이어리 조회 로직 (모든 다이어리 - 공개/비공개 모두)
        return diaryRepository.search("member_id:" + member.getId(), pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Diary> getUserPublicDiaries(Member author, Pageable pageable) {
        // 특정 사용자의 공개 다이어리만 조회
        return diaryRepository.search("member_id:" + author.getId() + " AND is_public:true", pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Diary> searchDiaries(String keyword, String areaCode, String sigunguCode, 
                                    Integer minPrice, Integer maxPrice, List<String> tags, Pageable pageable) {
        // 복합 검색 조건에 따른 검색 로직
        // 실제 구현은 리포지토리에 검색 메서드를 추가하거나 QueryDSL 등을 활용하여 구현
        
        // 태그로 검색하는 경우
        if (tags != null && !tags.isEmpty()) {
            return diaryRepository.searchByTags(tags, pageable);
        }
        
        // 지역 코드로 검색하는 경우
        if (areaCode != null && !areaCode.isEmpty()) {
            return diaryRepository.searchByAreaCodeAndSigunguCode(areaCode, sigunguCode, pageable);
        }
        
        // 가격 범위로 검색하는 경우
        if (minPrice != null && maxPrice != null) {
            return diaryRepository.searchByPriceRange(minPrice, maxPrice, pageable);
        }
        
        // 키워드로 검색하는 경우
        if (keyword != null && !keyword.isEmpty()) {
            return diaryRepository.search(keyword, pageable);
        }
        
        // 기본 검색 (전체 목록)
        return diaryRepository.search("is_public:true", pageable);
    }
}
