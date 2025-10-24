package com.passbee.license.dto;

import com.passbee.exam.ExamSubject;
import lombok.Builder;

@Builder
public record ExamSubjectResponseDto(
        Long id,
        String kmNm, // 과목명
        String kmYn // 필수과목여부
) {
    public static ExamSubjectResponseDto from(ExamSubject examSubject) {
        return ExamSubjectResponseDto.builder()
                .id(examSubject.getId())
                .kmNm(examSubject.getKmNm())
                .kmYn(examSubject.getKmYn())
                .build();
    }
}