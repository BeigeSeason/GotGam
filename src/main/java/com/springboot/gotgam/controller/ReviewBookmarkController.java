package com.springboot.gotgam.controller;


import com.springboot.gotgam.dto.ReviewReqDto;
import com.springboot.gotgam.dto.ReviewResDto;
import com.springboot.gotgam.service.BookmarkService;
import com.springboot.gotgam.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/review-bookmark")
@RequiredArgsConstructor
public class ReviewBookmarkController {
    private final ReviewService reviewService;
    private final BookmarkService bookmarkService;


    // 리뷰 작성
    @PostMapping("/reviews")
    public ResponseEntity<Void> addReviewRedis(@RequestBody ReviewReqDto reviewReqDto) {
        reviewService.addReviewAsync(reviewReqDto);
        return ResponseEntity.ok().build();
    }

    // 리뷰 수정
    @PutMapping("/review")
    public ResponseEntity<Void> editReviewRedis(@RequestBody ReviewReqDto reviewReqDto) {
        reviewService.editReviewAsync(reviewReqDto);
        return ResponseEntity.ok().build();
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReviewAsync(reviewId);
        return ResponseEntity.noContent().build();
    }


    // 북마크 추가
    @PostMapping("/bookmarks/add")
    public ResponseEntity<Boolean> addBookmark(@RequestParam String targetId,
                                               @RequestParam String userId,
                                               @RequestParam String type) {
        bookmarkService.addBookmarkAsync(targetId, userId, type);
        return ResponseEntity.ok().build();
    }

    // 북마크 삭제
    @PostMapping("/users/{userId}/bookmarks/{targetId}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable String userId,
                                               @PathVariable String targetId) {
        bookmarkService.deleteBookmarkAsync(targetId, userId);
        return ResponseEntity.ok().build();
    }

    // 내가 북마크 여부 조회
    @GetMapping("/users/{userId}/bookmarks/{targetId}")
    public ResponseEntity<Boolean> isBookmarked(@PathVariable String userId,
                                                @PathVariable String targetId) {
        return ResponseEntity.ok(bookmarkService.isBookmarked(targetId, userId));
    }


    // 특정 관광지 리뷰 조회
    @GetMapping("/tourspots/{tourSpotId}/reviews")
    public ResponseEntity<Page<ReviewResDto>> getReviews(@RequestParam int page,
                                                         @RequestParam int size,
                                                         @PathVariable String tourSpotId) {
        return new ResponseEntity<>(reviewService.getReviews(page, size, tourSpotId), HttpStatus.OK);
    }

    // 내가 작성한 리뷰 조회
    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<Page<ReviewResDto>> getMyReviews(@PathVariable String userId,
                                                           @RequestParam int page,
                                                           @RequestParam int size) {
        return new ResponseEntity<>(reviewService.getMyReviews(page, size, userId), HttpStatus.OK);
    }
}

