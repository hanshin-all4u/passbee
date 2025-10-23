package com.passbee.qna.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull; // ▼▼▼ [추가]
import jakarta.validation.constraints.Size;

public record QnaRequestDto(
        @NotBlank(message = "제목을 입력해주세요.")
        @Size(min = 2, max = 255, message = "제목은 2자 이상 255자 이하로 작성해주세요.")
        String title,

        @NotBlank(message = "질문 내용을 입력해주세요.")
        @Size(min = 5, message = "내용은 5자 이상 작성해주세요.")
        String content,

        // ▼▼▼ [추가] 비밀글 여부 (true / false) ▼▼▼
        @NotNull(message = "비밀글 여부를 선택해주세요.")
        boolean secret
) {
}