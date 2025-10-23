package com.passbee.comment.dto;

import com.passbee.comment.domain.Comment;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponseDto(
        Long id,
        Long reviewId,
        String authorNickname,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // Comment 엔티티를 DTO로 변환
    public static CommentResponseDto from(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .reviewId(comment.getReview().getReviewId())
                .authorNickname(comment.getUser().getNickname())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}