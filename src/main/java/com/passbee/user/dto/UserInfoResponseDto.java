package com.passbee.user.dto;

import com.passbee.user.Users;
import lombok.Builder;

@Builder
public record UserInfoResponseDto(
        Long userId,
        String email,
        String name,
        String nickname,
        String role
) {
    public static UserInfoResponseDto from(Users user) {
        return UserInfoResponseDto.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .nickname(user.getNickname())
                .role(user.getRole().name())
                .build();
    }
}