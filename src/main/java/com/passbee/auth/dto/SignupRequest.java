package com.passbee.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size; // ▼▼▼ [추가]

public record SignupRequest(
        @NotBlank(message = "이름을 입력해주세요.") // ▼▼▼ [수정] 메시지 추가
        String name,

        // ▼▼▼ [추가] 닉네임 필드 ▼▼▼
        @NotBlank(message = "닉네임을 입력해주세요.")
        @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
        String nickname,

        @Email @NotBlank(message = "이메일을 입력해주세요.") // ▼▼▼ [수정] 메시지 추가
        String email,

        @NotBlank(message = "비밀번호를 입력해주세요.") // ▼▼▼ [수정] 메시지 추가
        String password
) {}