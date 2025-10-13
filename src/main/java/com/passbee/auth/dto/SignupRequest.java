package com.passbee.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SignupRequest(

        @NotBlank(message = "닉네임은 필수 입력값입니다.")
        String nickname,

        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,

        @Email(message = "올바른 이메일 형식이 아닙니다.")
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password
) {}
