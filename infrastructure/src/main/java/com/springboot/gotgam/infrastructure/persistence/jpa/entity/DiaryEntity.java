package com.springboot.gotgam.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 다이어리 JPA 엔티티
 */
@Entity
@Table(name = "diaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryEntity {
    
    @Id
    @Column(name = "diary_id")
    private String id;
    
    @Column(nullable = false)
    private String title;
    
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    
    @Column(name = "is_public", nullable = false)
    private boolean isPublic;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;
    
    private String region;
    
    @Column(name = "area_code")
    private String areaCode;
    
    @Column(name = "sigungu_code")
    private String sigunguCode;
    
    @Column(name = "total_cost")
    private int totalCost;
    
    @ElementCollection
    @CollectionTable(
        name = "diary_tags",
        joinColumns = @JoinColumn(name = "diary_id")
    )
    @Column(name = "tag")
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
