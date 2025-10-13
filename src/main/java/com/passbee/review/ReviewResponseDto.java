package com.passbee.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

// 저장된 리뷰를 사용자에게 보여줄 때 사용하는 DTO
@Getter
public class ReviewResponseDto {
    private final Long id;
    private final String comment;
    private final int rating;
    private final Review.Difficulty difficulty;
    private final String authorName; // 작성자 이름
    private final LocalDateTime createdAt;

    @Builder
    public ReviewResponseDto(Long id, String comment, int rating, Review.Difficulty difficulty, String authorName, LocalDateTime createdAt) {
        this.id = id;
        this.comment = comment;
        this.rating = rating;
        this.difficulty = difficulty;
        this.authorName = authorName;
        this.createdAt = createdAt;
    }

    // Review(Entity)를 ReviewResponseDto로 변환하는 정적 메소드
    public static ReviewResponseDto fromEntity(Review review) {
        return ReviewResponseDto.builder()
                .id(review.getReviewId())
                .comment(review.getComment())
                .rating(review.getRating())
                .difficulty(review.getDifficulty())
                .authorName(review.getUser().getNickname()) // User 엔티티에서 닉네임 가져오기
                .createdAt(review.getCreatedAt())
                .build();
    }
}
