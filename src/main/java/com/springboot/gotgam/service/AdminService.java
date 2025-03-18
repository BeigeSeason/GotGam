package com.springboot.gotgam.service;

import com.springboot.gotgam.constant.MemberRole;
import com.springboot.gotgam.constant.State;
import com.springboot.gotgam.dto.Auth.MemberResDto;
import com.springboot.gotgam.dto.report.ReportManageReq;
import com.springboot.gotgam.entity.mysql.Ban;
import com.springboot.gotgam.entity.mysql.Member;
import com.springboot.gotgam.entity.mysql.Report;
import com.springboot.gotgam.repository.BanRepository;
import com.springboot.gotgam.repository.DiaryRepository;
import com.springboot.gotgam.repository.MemberRepository;
import com.springboot.gotgam.repository.ReportRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AdminService {
    private final MemberRepository memberRepository;
    private final DiaryRepository diaryRepository;
    private final ReportRepository reportRepository;
    private final BanRepository banRepository;
    private final DiaryService diaryService;
    private final ReviewService reviewService;

    // 회원 전체 조회
    public Page<MemberResDto> getMemberAllList(int page, int size, String searchType, String searchValue, Boolean type, String sort) {
        Sort sortBy = Sort.by("id").descending();

        // type이 있을 경우 정렬
        if (type != null) {
            if (type) {
                sortBy = Sort.by("banned").descending();
            } else {
                sortBy = Sort.by("banned").ascending();
            }
            if (sort != null) {
                switch (sort) {
                    case "idAsc":
                        sortBy = sortBy.and(Sort.by("id").ascending());
                        break;
                    case "idDesc":
                        sortBy = sortBy.and(Sort.by("id").descending());
                        break;
                    case "userIdAsc":
                        sortBy = sortBy.and(Sort.by("userId").ascending());
                        break;
                    case "userIdDesc":
                        sortBy = sortBy.and(Sort.by("userId").descending());
                        break;
                    default:
                        break;
                }
            }
        }

        // type이 없고 sort만 있을 때 정렬
        if (type == null && sort != null) {
            switch (sort) {
                case "idAsc":
                    sortBy = Sort.by("id").ascending();
                    break;
                case "idDesc":
                    sortBy = Sort.by("id").descending();
                    break;
                case "userIdAsc":
                    sortBy = Sort.by("userId").ascending();
                    break;
                case "userIdDesc":
                    sortBy = Sort.by("userId").descending();
                    break;
                default:
                    break;
            }
        }

        Pageable pageable = PageRequest.of(page, size, sortBy);

        Page<Member> memberPage;
        if (searchType != null && searchValue != null && !searchValue.isEmpty()) {
            switch (searchType) {
                case "ID":
                    memberPage = memberRepository.findByUserIdContaining(searchValue, pageable);
                    break;
                case "NAME":
                    memberPage = memberRepository.findByNameContaining(searchValue, pageable);
                    break;
                case "NICKNAME":
                    memberPage = memberRepository.findByNicknameContaining(searchValue, pageable);
                    break;
                case "EMAIL":
                    memberPage = memberRepository.findByEmailContaining(searchValue, pageable);
                    break;
                default:
                    memberPage = memberRepository.findAll(pageable);  // 기본값: 전체 조회
                    break;
            }
        } else {
            memberPage = memberRepository.findAll(pageable);  // 검색 조건이 없으면 모든 멤버 조회
        }

        return memberPage.map(Member::convertEntityToDto);
    }

    // 신고 관리
    @Transactional
    public boolean reportProcess(ReportManageReq request) {
        try {
            // 신고 처리
            Report report = reportRepository.findById(request.getReportId())
                    .orElseThrow(() -> new RuntimeException("해당 신고가 존재하지 않습니다."));
            report.setState(request.isState() ? State.ACCEPT : State.REJECT);
            reportRepository.save(report);

            // 유저 정지
            if (request.getUserId() != null) {
                memberBan(request.getUserId(), request.getDay(), request.getReason());
            }

            // 신고 승인 -> 일지or댓글 삭제
            if (request.isState()) {
                if (request.getDiaryId() != null) {
                    diaryService.deleteDiary(request.getDiaryId());
                }
                if (request.getReviewId() != null) {
                    reviewService.deleteReview(request.getReviewId());
                }
            }

            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // 유저 정지 관리
    @Transactional
    public boolean memberBan(Long id, int day, String reason) {
        try {
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("해당 유저를 찾을 수 없습니다."));
            Ban alreadyBanned = banRepository.findFirstByMemberOrderByIdDesc(member);

            LocalDateTime endDate;

            // 유저 상태 변경, 종료 날짜 설정
            if (alreadyBanned == null) {
                endDate = LocalDateTime.now().plusDays(day).with(LocalTime.of(0, 0));
                member.setBanned(true);
                member.setRole(MemberRole.BANNED);
                memberRepository.save(member);
            } else {
                endDate = alreadyBanned.getEndDate().plusDays(day).with(LocalTime.of(0, 0));
            }

            // ban 추가
            Ban ban = Ban.builder()
                    .member(member)
                    .startDate(LocalDateTime.now())
                    .endDate(endDate)
                    .reason(reason)
                    .build();
            banRepository.save(ban);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    // 월별 유저 통계
    public List<Integer> getMonthlyStats(String type, int year) throws IllegalAccessException {
        List<Integer> signups = new ArrayList<>(Collections.nCopies(12, 0));

        List<Object[]> rawData = switch (type) {
            case "user" -> memberRepository.getMonthlySignups(year);
            case "diary" -> diaryRepository.getMonthlyDiaryCounts(year);
            case "report" -> reportRepository.getMonthlyReportCounts(year);
            default -> throw new IllegalAccessException("타입이 없음: " + type);
        };

        for (Object[] row : rawData) {
            int month = (int) row[0];
            int count = ((Number) row[1]).intValue();
            signups.set(month - 1, count);
        }

        return signups;
    }
}
