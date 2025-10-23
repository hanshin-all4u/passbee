package com.passbee.qna.dto;

import com.passbee.qna.domain.Qna;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record QnaResponseDto(
        Long id,
        String authorNickname,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        // ▼▼▼ [추가] 비밀글 여부 ▼▼▼
        boolean secret
) {
    // Qna 엔티티를 DTO로 변환
    public static QnaResponseDto from(Qna qna) {
        return QnaResponseDto.builder()
                .id(qna.getId())
                .authorNickname(qna.getUser().getNickname())
                .title(qna.getTitle())
                .content(qna.getContent())
                .createdAt(qna.getCreatedAt())
                .updatedAt(qna.getUpdatedAt())
                .secret(qna.isSecret()) // ▼▼▼ [추가]
                .build();
    }
}