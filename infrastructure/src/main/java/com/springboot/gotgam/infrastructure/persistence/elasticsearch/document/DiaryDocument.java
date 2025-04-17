package com.springboot.gotgam.infrastructure.persistence.elasticsearch.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 다이어리 Elasticsearch 문서 모델
 */
@Document(indexName = "diaries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDocument {
    
    @Id
    private String diaryId;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;
    
    @Field(type = FieldType.Boolean)
    private boolean isPublic;
    
    @Field(type = FieldType.Date)
    private LocalDate startDate;
    
    @Field(type = FieldType.Date)
    private LocalDate endDate;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String region;
    
    @Field(type = FieldType.Keyword)
    private String areaCode;
    
    @Field(type = FieldType.Keyword)
    private String sigunguCode;
    
    @Field(type = FieldType.Integer)
    private int totalCost;
    
    @Field(type = FieldType.Long)
    private Long memberId;
    
    @Field(type = FieldType.Keyword)
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    @Field(type = FieldType.Date)
    private LocalDateTime createdTime;
    
    @Field(type = FieldType.Date)
    private LocalDateTime updatedTime;
}
