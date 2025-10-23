package com.passbee.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordUpdateRequestDto(
        @NotBlank(message = "현재 비밀번호를 입력해주세요.")
        String oldPassword,

        @NotBlank(message = "새 비밀번호를 입력해주세요.")
        @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하로 입력해주세요.")
        String newPassword
) {
}