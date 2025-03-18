package com.springboot.gotgam.entity.mysql;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.springboot.gotgam.constant.Type;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "bookmark")
@ToString
@Getter
@Setter
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member")
    @JsonIgnore
    private Member member;

    private Type type; // MEMBER, DIARY, REVIEW, TOURSPOT

    private String bookmarkedId; // Diary 아이디, TourSpot 아이디
}
