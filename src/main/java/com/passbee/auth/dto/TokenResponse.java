package com.passbee.auth.dto;

public record TokenResponse(String accessToken, String userName, String email) {}
