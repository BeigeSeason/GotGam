package com.springboot.gotgam.repository;

import com.springboot.gotgam.constant.Type;
import com.springboot.gotgam.entity.mysql.Bookmark;
import com.springboot.gotgam.entity.mysql.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.bookmarkedId = :tourSpotId")
    Integer countByBookmarkedId(@Param("tourSpotId") String tourSpotId);

    @Query("SELECT b.bookmarkedId, COUNT(b) FROM Bookmark b WHERE b.bookmarkedId IN :ids GROUP BY b.bookmarkedId")
    List<Object[]> findBookmarkCountsByTourSpotIds(@Param("ids") List<String> tourSpotIds);

    Optional<Bookmark> findByMemberAndBookmarkedId(Member member, String bookmarkedId);
    List<Bookmark> findByMemberAndType(Member member, Type type);
}
