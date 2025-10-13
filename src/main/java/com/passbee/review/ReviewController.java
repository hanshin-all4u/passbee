package com.passbee.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/licenses/{jmcd}/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "자격증별 후기(리뷰) API")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    @Operation(summary = "특정 자격증의 모든 리뷰 조회")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByLicense(@PathVariable String jmcd) {
        List<ReviewResponseDto> reviews = reviewService.findReviewsByLicense(jmcd);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping
    @Operation(summary = "특정 자격증에 새로운 리뷰 작성")
    public ResponseEntity<ReviewResponseDto> createReview(
            @PathVariable String jmcd,
            @RequestBody ReviewRequestDto requestDto
    ) {
        // TODO: 현재는 임시로 사용자 ID를 1로 고정합니다.
        //       추후 Spring Security를 통해 실제 로그인된 사용자 ID를 가져와야 합니다.
        Long tempUserId = 1L;
        ReviewResponseDto createdReview = reviewService.createReview(jmcd, requestDto, tempUserId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }
}
