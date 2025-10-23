package com.passbee.review.dto;

import com.passbee.review.Difficulty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewRequestDto(
        @NotNull(message = "난이도를 선택해주세요.")
        Difficulty difficulty,

        @NotBlank(message = "후기 내용을 입력해주세요.")
        @Size(min = 10, max = 5000, message = "후기는 10자 이상 5000자 이하로 작성해주세요.")
        String comment
) {
}