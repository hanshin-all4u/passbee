package com.passbee.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NicknameUpdateRequestDto(
        @NotBlank(message = "새 닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
        String nickname,

        @NotBlank(message = "본인 확인을 위해 현재 비밀번호를 입력해주세요.")
                String password
) {
}