package com.passbee.auth.dto;

public record AuthResponse(
        String accessToken,
        long expiresInMs
) {}

