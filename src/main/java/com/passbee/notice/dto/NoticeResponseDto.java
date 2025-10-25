package com.passbee.notice.dto;

import com.passbee.notice.domain.Notice;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record NoticeResponseDto(
        Long id,
        String authorNickname, // 작성자 닉네임
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static NoticeResponseDto from(Notice notice) {
        return NoticeResponseDto.builder()
                .id(notice.getId())
                .authorNickname(notice.getAuthor().getNickname()) // Users 엔티티에서 닉네임 가져오기
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt()) // BaseTimeEntity 상속
                .updatedAt(notice.getUpdatedAt()) // BaseTimeEntity 상속
                .build();
    }
}