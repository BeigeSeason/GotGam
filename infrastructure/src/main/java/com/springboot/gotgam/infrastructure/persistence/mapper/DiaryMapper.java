package com.springboot.gotgam.infrastructure.persistence.mapper;

import com.springboot.gotgam.domain.diary.model.Diary;
import com.springboot.gotgam.domain.member.model.Member;
import com.springboot.gotgam.infrastructure.persistence.elasticsearch.document.DiaryDocument;
import com.springboot.gotgam.infrastructure.persistence.jpa.entity.DiaryEntity;
import com.springboot.gotgam.infrastructure.persistence.jpa.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 다이어리 도메인 모델과 JPA 엔티티, Elasticsearch 문서 간 변환 매퍼
 */
@Component
@RequiredArgsConstructor
public class DiaryMapper {
    
    private final MemberMapper memberMapper;
    
    /**
     * JPA 엔티티를 도메인 모델로 변환
     */
    public Diary toDomain(DiaryEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Member author = memberMapper.toDomain(entity.getMember());
        
        return Diary.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .isPublic(entity.isPublic())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .region(entity.getRegion())
                .areaCode(entity.getAreaCode())
                .sigunguCode(entity.getSigunguCode())
                .totalCost(entity.getTotalCost())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .author(author)
                .tags(entity.getTags())
                .build();
    }
    
    /**
     * 도메인 모델을 JPA 엔티티로 변환
     */
    public DiaryEntity toEntity(Diary domain) {
        if (domain == null) {
            return null;
        }
        
        return DiaryEntity.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .content(domain.getContent())
                .isPublic(domain.isPublic())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .region(domain.getRegion())
                .areaCode(domain.getAreaCode())
                .sigunguCode(domain.getSigunguCode())
                .totalCost(domain.getTotalCost())
                .tags(domain.getTags())
                .build();
    }
    
    /**
     * Elasticsearch 문서를 도메인 모델로 변환
     */
    public Diary toDomain(DiaryDocument document) {
        if (document == null) {
            return null;
        }
        
        return Diary.builder()
                .id(document.getDiaryId())
                .title(document.getTitle())
                .content(document.getContent())
                .isPublic(document.isPublic())
                .startDate(document.getStartDate())
                .endDate(document.getEndDate())
                .region(document.getRegion())
                .areaCode(document.getAreaCode())
                .sigunguCode(document.getSigunguCode())
                .totalCost(document.getTotalCost())
                .createdAt(document.getCreatedTime())
                .updatedAt(document.getUpdatedTime())
                .tags(document.getTags())
                .build();
    }
    
    /**
     * 도메인 모델을 Elasticsearch 문서로 변환
     */
    public DiaryDocument toDocument(Diary domain) {
        if (domain == null) {
            return null;
        }
        
        return DiaryDocument.builder()
                .diaryId(domain.getId())
                .title(domain.getTitle())
                .content(domain.getContent())
                .isPublic(domain.isPublic())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .region(domain.getRegion())
                .areaCode(domain.getAreaCode())
                .sigunguCode(domain.getSigunguCode())
                .totalCost(domain.getTotalCost())
                .memberId(domain.getAuthor().getId())
                .tags(domain.getTags())
                .createdTime(domain.getCreatedAt())
                .updatedTime(domain.getUpdatedAt())
                .build();
    }
}
