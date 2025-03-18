package com.springboot.gotgam.dto.diary;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiarySearchListDto {
    private String diaryId; // 다이어리 아이디
    private String title; // 제목
    private String contentSummary; // 내용 요약
    private String thumbnail; // 썸네일
    private String writer; // 작성자
    private String writerImg; // 작성자 프로필 이미지
    private LocalDateTime createdAt; // 작성일
    private LocalDate startDate; // 일정 시작일
    private LocalDate endDate;   // 일정 종료일
    private String region;      // 지역
}
