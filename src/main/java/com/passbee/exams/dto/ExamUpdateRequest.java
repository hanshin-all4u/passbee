package com.passbee.exams.dto;

import jakarta.validation.constraints.NotBlank;
public record ExamUpdateRequest(@NotBlank String title, String date, String org, String link, String description) {}
