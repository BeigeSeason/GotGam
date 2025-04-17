package com.springboot.gotgam.infrastructure.persistence.mapper;

import com.springboot.gotgam.domain.member.model.Member;
import com.springboot.gotgam.infrastructure.persistence.jpa.entity.MemberEntity;
import org.springframework.stereotype.Component;

/**
 * 회원 도메인 모델과 JPA 엔티티 간 변환 매퍼
 */
@Component
public class MemberMapper {
    
    /**
     * JPA 엔티티를 도메인 모델로 변환
     */
    public Member toDomain(MemberEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return Member.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .email(entity.getEmail())
                .nickname(entity.getNickname())
                .password(entity.getPassword())
                .imgPath(entity.getImgPath())
                .role(entity.getRole())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    /**
     * 도메인 모델을 JPA 엔티티로 변환
     */
    public MemberEntity toEntity(Member domain) {
        if (domain == null) {
            return null;
        }
        
        return MemberEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .email(domain.getEmail())
                .nickname(domain.getNickname())
                .password(domain.getPassword())
                .imgPath(domain.getImgPath())
                .role(domain.getRole())
                .build();
    }
}
