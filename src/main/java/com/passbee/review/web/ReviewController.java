package com.passbee.review.web;

import com.passbee.review.dto.ReviewRequestDto;
import com.passbee.review.dto.ReviewResponseDto;
import com.passbee.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Reviews", description = "시험 후기 (리뷰) API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api") // API 경로 통일성을 위해 /api로 시작
public class ReviewController {

    private final ReviewService reviewService;

    // 1. 후기 작성 (특정 자격증에 대해)
    @Operation(summary = "특정 자격증에 후기 작성")
    @PostMapping("/licenses/{jmcd}/reviews")
    public ResponseEntity<?> createReview(
            @PathVariable String jmcd,
            @Valid @RequestBody ReviewRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        ReviewResponseDto createdReview = reviewService.createReview(userDetails.getUsername(), jmcd, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    // 2. 특정 자격증의 모든 후기 목록 조회
    @Operation(summary = "특정 자격증의 모든 후기 목록 조회")
    @GetMapping("/licenses/{jmcd}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByLicense(@PathVariable String jmcd) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByLicense(jmcd);
        return ResponseEntity.ok(reviews);
    }

    // 3. 단일 후기 상세 조회
    @Operation(summary = "단일 후기 상세 조회")
    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReview(@PathVariable Long reviewId) {
        ReviewResponseDto review = reviewService.getReview(reviewId);
        return ResponseEntity.ok(review);
    }

    // 4. 후기 수정
    @Operation(summary = "후기 수정 (본인만 가능)")
    @PatchMapping("/reviews/{reviewId}")
    public ResponseEntity<?> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        ReviewResponseDto updatedReview = reviewService.updateReview(userDetails.getUsername(), reviewId, requestDto);
        return ResponseEntity.ok(updatedReview);
    }

    // 5. 후기 삭제
    @Operation(summary = "후기 삭제 (본인만 가능)")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        reviewService.deleteReview(userDetails.getUsername(), reviewId);
        return ResponseEntity.noContent().build(); // 204 No Content (성공적으로 삭제됨)
    }
}