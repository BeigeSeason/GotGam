package com.springboot.gotgam.api.diary.dto;

import com.springboot.gotgam.api.member.dto.MemberSummaryResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 다이어리 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryResponse {
    
    private String id;
    private String title;
    private String content;
    private boolean isPublic;
    private LocalDate startDate;
    private LocalDate endDate;
    private String region;
    private String areaCode;
    private String sigunguCode;
    private int totalCost;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private MemberSummaryResponse author;
    
    @Builder.Default
    private List<String> tags = new ArrayList<>();
}
