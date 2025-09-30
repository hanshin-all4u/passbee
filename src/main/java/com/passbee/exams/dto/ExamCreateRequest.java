package com.passbee.exams.dto;

import jakarta.validation.constraints.NotBlank;
public record ExamCreateRequest(@NotBlank String title, String date, String org, String link, String description) {}
