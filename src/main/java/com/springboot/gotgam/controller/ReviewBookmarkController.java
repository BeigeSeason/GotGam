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
@CrossOrigin(origins = "http://localhost:3000")
public class ReviewBookmarkController {
    private final ReviewService reviewService;
    private final BookmarkService bookmarkService;


    // 리뷰 작성
    @PostMapping("/add-review")
    public ResponseEntity<Void> addReviewRedis(@RequestBody ReviewReqDto reviewReqDto) {
        reviewService.addReviewAsync(reviewReqDto);
        return ResponseEntity.ok().build();
    }

    // 리뷰 수정
    @PostMapping("/edit-review")
    public ResponseEntity<Void> editReviewRedis(@RequestBody ReviewReqDto reviewReqDto) {
        reviewService.editReviewAsync(reviewReqDto);
        return ResponseEntity.ok().build();
    }

    // 리뷰 삭제
    @PostMapping("/delete-review-redis")
    public ResponseEntity<Void> deleteReviewRedis(@RequestParam Long reviewId) {
        reviewService.deleteReviewAsync(reviewId);
        return ResponseEntity.ok().build();
    }

    // 북마크 추가
    @PostMapping("/add-bookmark")
    public ResponseEntity<Boolean> addBookmark(@RequestParam String targetId,
                                               @RequestParam String userId,
                                               @RequestParam String type) {
        bookmarkService.addBookmarkAsync(targetId, userId, type);
        return ResponseEntity.ok().build();
    }

    // 북마크 삭제
    @PostMapping("/delete-bookmark")
    public ResponseEntity<Boolean> deleteBookmark(@RequestParam String targetId, @RequestParam String userId) {
        bookmarkService.deleteBookmarkAsync(targetId, userId);
        return ResponseEntity.ok().build();
    }

    // 내가 북마크 여부 조회
    @GetMapping("/my-bookmark")
    public ResponseEntity<Boolean> isBookmarked(@RequestParam String targetId, @RequestParam String userId) {
        return ResponseEntity.ok(bookmarkService.isBookmarked(targetId, userId));
    }


    // 리뷰 조회
    @GetMapping("/review-list")
    public ResponseEntity<Page<ReviewResDto>> getReviews(@RequestParam int page,
                                                         @RequestParam int size,
                                                         @RequestParam String tourSpotId) {
        return new ResponseEntity<>(reviewService.getReviews(page, size, tourSpotId), HttpStatus.OK);
    }
}
