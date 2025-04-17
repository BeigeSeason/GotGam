package com.springboot.gotgam.infrastructure.persistence;

import com.springboot.gotgam.domain.diary.model.Diary;
import com.springboot.gotgam.domain.diary.repository.DiaryRepository;
import com.springboot.gotgam.domain.member.model.Member;
import com.springboot.gotgam.infrastructure.persistence.elasticsearch.document.DiaryDocument;
import com.springboot.gotgam.infrastructure.persistence.elasticsearch.repository.ElasticsearchDiaryRepository;
import com.springboot.gotgam.infrastructure.persistence.jpa.entity.DiaryEntity;
import com.springboot.gotgam.infrastructure.persistence.jpa.entity.MemberEntity;
import com.springboot.gotgam.infrastructure.persistence.jpa.repository.JpaDiaryRepository;
import com.springboot.gotgam.infrastructure.persistence.jpa.repository.JpaMemberRepository;
import com.springboot.gotgam.infrastructure.persistence.mapper.DiaryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 도메인 레이어의 DiaryRepository 인터페이스 구현체
 * JPA와 Elasticsearch 리포지토리를 조합하여 사용
 */
@Repository
@RequiredArgsConstructor
public class DiaryRepositoryImpl implements DiaryRepository {

    private final JpaDiaryRepository jpaDiaryRepository;
    private final ElasticsearchDiaryRepository elasticsearchDiaryRepository;
    private final JpaMemberRepository jpaMemberRepository;
    private final DiaryMapper diaryMapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Diary> findById(String id) {
        return jpaDiaryRepository.findById(id)
                .map(diaryMapper::toDomain);
    }

    @Override
    @Transactional
    public Diary save(Diary diary) {
        // 1. JPA 엔티티로 변환하여 저장
        MemberEntity memberEntity = jpaMemberRepository.findById(diary.getAuthor().getId())
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        
        DiaryEntity diaryEntity = diaryMapper.toEntity(diary);
        diaryEntity.setMember(memberEntity);
        
        DiaryEntity savedEntity = jpaDiaryRepository.save(diaryEntity);
        
        // 2. Elasticsearch 문서로 변환하여 저장
        DiaryDocument diaryDocument = diaryMapper.toDocument(diary);
        elasticsearchDiaryRepository.save(diaryDocument);
        
        // 3. 저장된 엔티티를 도메인 객체로 변환하여 반환
        return diaryMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional
    public void delete(Diary diary) {
        // JPA 및 Elasticsearch에서 삭제
        jpaDiaryRepository.deleteById(diary.getId());
        elasticsearchDiaryRepository.deleteById(diary.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Diary> findAllByAuthor(Member author) {
        MemberEntity memberEntity = jpaMemberRepository.findById(author.getId())
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
                
        return jpaDiaryRepository.findAllByMember(memberEntity).stream()
                .map(diaryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Diary> findAllByAuthorAndIsPublic(Member author, boolean isPublic) {
        MemberEntity memberEntity = jpaMemberRepository.findById(author.getId())
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
                
        return jpaDiaryRepository.findAllByMemberAndIsPublic(memberEntity, isPublic).stream()
                .map(diaryMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Diary> search(String keyword, Pageable pageable) {
        // Elasticsearch를 사용한 검색 (더 효율적인 전문 검색)
        return elasticsearchDiaryRepository.searchByKeyword(keyword, pageable)
                .map(diaryMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Diary> searchByTags(List<String> tags, Pageable pageable) {
        // Elasticsearch를 사용한 태그 검색
        return elasticsearchDiaryRepository.findByTags(tags, pageable)
                .map(diaryMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Diary> searchByAreaCodeAndSigunguCode(String areaCode, String sigunguCode, Pageable pageable) {
        // Elasticsearch를 사용한 지역 코드 검색
        return elasticsearchDiaryRepository.findByAreaCodeAndSigunguCodeAndIsPublic(areaCode, sigunguCode, true, pageable)
                .map(diaryMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Diary> searchByPriceRange(int minPrice, int maxPrice, Pageable pageable) {
        // Elasticsearch를 사용한 가격 범위 검색
        return elasticsearchDiaryRepository.findByPriceRange(minPrice, maxPrice, pageable)
                .map(diaryMapper::toDomain);
    }
}
