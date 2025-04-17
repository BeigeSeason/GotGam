package com.springboot.gotgam.domain.diary.model;

import com.springboot.gotgam.domain.member.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 다이어리 도메인 모델
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Diary {
    
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
    private Member author;
    
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    /**
     * 다이어리 공개 여부 변경
     */
    public void changeVisibility(boolean isPublic) {
        this.isPublic = isPublic;
    }
    
    /**
     * 다이어리 내용 수정
     */
    public void update(String title, String content, LocalDate startDate, LocalDate endDate,
                      String region, String areaCode, String sigunguCode, int totalCost, List<String> tags) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.endDate = endDate;
        this.region = region;
        this.areaCode = areaCode;
        this.sigunguCode = sigunguCode;
        this.totalCost = totalCost;
        this.tags = tags;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 특정 사용자가 이 다이어리의 소유자인지 확인
     */
    public boolean isOwnedBy(Member member) {
        return this.author.equals(member);
    }
}
