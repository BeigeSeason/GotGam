package com.springboot.gotgam.api.diary.mapper;

import com.springboot.gotgam.api.diary.dto.DiaryCreateRequest;
import com.springboot.gotgam.api.diary.dto.DiaryResponse;
import com.springboot.gotgam.api.diary.dto.DiaryUpdateRequest;
import com.springboot.gotgam.api.member.dto.MemberSummaryResponse;
import com.springboot.gotgam.domain.diary.model.Diary;
import org.springframework.stereotype.Component;

/**
 * 다이어리 DTO와 도메인 모델 간 변환 매퍼
 */
@Component
public class DiaryDtoMapper {
    
    /**
     * 도메인 모델을 응답 DTO로 변환
     */
    public DiaryResponse toResponse(Diary diary) {
        if (diary == null) {
            return null;
        }
        
        // 작성자 정보 변환
        MemberSummaryResponse authorResponse = null;
        if (diary.getAuthor() != null) {
            authorResponse = MemberSummaryResponse.builder()
                    .id(diary.getAuthor().getId().toString())
                    .userId(diary.getAuthor().getUserId())
                    .nickname(diary.getAuthor().getNickname())
                    .imgPath(diary.getAuthor().getImgPath())
                    .build();
        }
        
        return DiaryResponse.builder()
                .id(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .isPublic(diary.isPublic())
                .startDate(diary.getStartDate())
                .endDate(diary.getEndDate())
                .region(diary.getRegion())
                .areaCode(diary.getAreaCode())
                .sigunguCode(diary.getSigunguCode())
                .totalCost(diary.getTotalCost())
                .createdAt(diary.getCreatedAt())
                .updatedAt(diary.getUpdatedAt())
                .author(authorResponse)
                .tags(diary.getTags())
                .build();
    }
}
