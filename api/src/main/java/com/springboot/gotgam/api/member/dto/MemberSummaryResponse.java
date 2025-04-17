package com.springboot.gotgam.api.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 요약 정보 응답 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSummaryResponse {
    
    private String id;
    private String userId;
    private String nickname;
    private String imgPath;
}
