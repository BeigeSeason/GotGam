package com.springboot.gotgam.api.diary.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 다이어리 생성 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryCreateRequest {
    
    @NotBlank(message = "제목은 필수입니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수입니다.")
    private String content;
    
    @NotNull(message = "공개 여부는 필수입니다.")
    private boolean isPublic;
    
    @NotNull(message = "시작 날짜는 필수입니다.")
    private LocalDate startDate;
    
    @NotNull(message = "종료 날짜는 필수입니다.")
    private LocalDate endDate;
    
    private String region;
    
    private String areaCode;
    
    private String sigunguCode;
    
    @Min(value = 0, message = "총 비용은 0 이상이어야 합니다.")
    private int totalCost;
    
    @Builder.Default
    private List<String> tags = new ArrayList<>();
}
