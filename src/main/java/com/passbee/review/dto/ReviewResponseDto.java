package com.passbee.review.dto;

import com.passbee.review.Difficulty;
import com.passbee.review.Review;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReviewResponseDto(
        Long reviewId,
        String authorNickname,
        String jmcd,
        String licenseName,
        Difficulty difficulty,
        String comment,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    // Entity를 DTO로 변환하는 정적 팩토리 메서드
    public static ReviewResponseDto from(Review review) {
        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .authorNickname(review.getUser().getNickname()) // Users 엔티티의 nickname 필드 사용
                .jmcd(review.getLicense().getJmcd())
                .licenseName(review.getLicense().getJmfldnm()) // License 엔티티의 jmfldnm 필드 사용
                .difficulty(review.getDifficulty())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}