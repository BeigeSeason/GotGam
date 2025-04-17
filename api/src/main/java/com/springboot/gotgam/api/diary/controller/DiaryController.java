package com.springboot.gotgam.api.diary.controller;

import com.springboot.gotgam.api.diary.dto.DiaryCreateRequest;
import com.springboot.gotgam.api.diary.dto.DiaryResponse;
import com.springboot.gotgam.api.diary.dto.DiaryUpdateRequest;
import com.springboot.gotgam.api.diary.mapper.DiaryDtoMapper;
import com.springboot.gotgam.domain.diary.model.Diary;
import com.springboot.gotgam.domain.diary.service.DiaryService;
import com.springboot.gotgam.domain.member.model.Member;
import com.springboot.gotgam.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 다이어리 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
public class DiaryController {
    
    private final DiaryService diaryService;
    private final MemberService memberService;
    private final DiaryDtoMapper diaryDtoMapper;
    
    /**
     * 다이어리 생성
     */
    @PostMapping
    public ResponseEntity<DiaryResponse> createDiary(
            @Valid @RequestBody DiaryCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Member currentUser = memberService.getMemberByUserId(userDetails.getUsername());
        
        Diary diary = diaryService.createDiary(
                request.getTitle(),
                request.getContent(),
                request.isPublic(),
                request.getStartDate(),
                request.getEndDate(),
                request.getRegion(),
                request.getAreaCode(),
                request.getSigunguCode(),
                request.getTotalCost(),
                request.getTags(),
                currentUser
        );
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(diaryDtoMapper.toResponse(diary));
    }
    
    /**
     * 다이어리 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiaryResponse> getDiary(@PathVariable String id) {
        Diary diary = diaryService.getDiaryById(id);
        return ResponseEntity.ok(diaryDtoMapper.toResponse(diary));
    }
    
    /**
     * 다이어리 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<DiaryResponse> updateDiary(
            @PathVariable String id,
            @Valid @RequestBody DiaryUpdateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Member currentUser = memberService.getMemberByUserId(userDetails.getUsername());
        
        Diary diary = diaryService.updateDiary(
                id,
                request.getTitle(),
                request.getContent(),
                request.isPublic(),
                request.getStartDate(),
                request.getEndDate(),
                request.getRegion(),
                request.getAreaCode(),
                request.getSigunguCode(),
                request.getTotalCost(),
                request.getTags(),
                currentUser
        );
        
        return ResponseEntity.ok(diaryDtoMapper.toResponse(diary));
    }
    
    /**
     * 다이어리 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiary(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Member currentUser = memberService.getMemberByUserId(userDetails.getUsername());
        diaryService.deleteDiary(id, currentUser);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 내 다이어리 목록 조회
     */
    @GetMapping("/my")
    public ResponseEntity<Page<DiaryResponse>> getMyDiaries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        Member currentUser = memberService.getMemberByUserId(userDetails.getUsername());
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<Diary> diaries = diaryService.getMyDiaries(currentUser, pageable);
        return ResponseEntity.ok(diaries.map(diaryDtoMapper::toResponse));
    }
    
    /**
     * 사용자의 공개 다이어리 목록 조회
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<DiaryResponse>> getUserPublicDiaries(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Member member = memberService.getMemberByUserId(userId);
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<Diary> diaries = diaryService.getUserPublicDiaries(member, pageable);
        return ResponseEntity.ok(diaries.map(diaryDtoMapper::toResponse));
    }
    
    /**
     * 다이어리 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<DiaryResponse>> searchDiaries(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String areaCode,
            @RequestParam(required = false) String sigunguCode,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") String direction) {
        
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<Diary> diaries = diaryService.searchDiaries(keyword, areaCode, sigunguCode, minPrice, maxPrice, tags, pageable);
        return ResponseEntity.ok(diaries.map(diaryDtoMapper::toResponse));
    }
}
