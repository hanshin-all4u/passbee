package com.passbee.notice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoticeRequestDto(
        @NotBlank(message = "제목을 입력해주세요.")
        @Size(min = 2, max = 255, message = "제목은 2자 이상 255자 이하로 작성해주세요.")
        String title,

        @NotBlank(message = "내용을 입력해주세요.")
        String content
) {
}