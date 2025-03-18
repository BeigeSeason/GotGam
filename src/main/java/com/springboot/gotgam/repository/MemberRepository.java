package com.springboot.gotgam.repository;

import com.springboot.gotgam.entity.mysql.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUserId(String userId);

    boolean existsMemberByUserId(String userId);

    Page<Member> findByUserIdContaining(String userId, Pageable pageable);
    Page<Member> findByNameContaining(String name, Pageable pageable);
    Page<Member> findByNicknameContaining(String nickname, Pageable pageable);
    Page<Member> findByEmailContaining(String email, Pageable pageable);

    List<Member> findByIdIn(Collection<Long> ids);

    boolean existsByUserId(String userId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByNameAndEmail(String name, String email);

    Optional<Member> findByUserIdAndEmail(String id, String email);

    // 월별 가입자 수 조회
    @Query("SELECT MONTH(m.regDate), COUNT(m) " +
            "FROM Member m " +
            "WHERE YEAR(m.regDate) = :year " +
            "GROUP BY MONTH(m.regDate) " +
            "ORDER BY MONTH(m.regDate)")
    List<Object[]> getMonthlySignups(@Param("year") int year);
}
