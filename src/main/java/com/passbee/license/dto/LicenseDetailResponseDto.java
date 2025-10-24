package com.passbee.license.dto;

import com.passbee.exam.ExamSubject;
import com.passbee.exam.PracticalItem;
import com.passbee.license.License;
import lombok.Builder;

import java.util.List;
import java.util.stream.Collectors;

@Builder
public record LicenseDetailResponseDto(
        // License 기본 정보
        String jmcd,
        String jmfldnm, // 자격증명
        String seriesnm,  // 분야 (예: 국가기술자격)
        String qualgbnm,  // 자격구분 (예: 기술자격)
        String summary,   // 개요
        String job,       // 수행직무
        String career,    // 진로 및 전망
        String trend,     // 출제경향

        // 1. 시험 과목 목록
        List<ExamSubjectResponseDto> subjects,

        // 2. 실기 지참물 목록
        List<PracticalItemResponseDto> practicalItems
) {
    public static LicenseDetailResponseDto from(
            License license,
            List<ExamSubject> subjects,
            List<PracticalItem> practicalItems) {

        // List<ExamSubject> -> List<ExamSubjectResponseDto>
        List<ExamSubjectResponseDto> subjectDtos = subjects.stream()
                .map(ExamSubjectResponseDto::from)
                .collect(Collectors.toList());

        // List<PracticalItem> -> List<PracticalItemResponseDto>
        List<PracticalItemResponseDto> practicalItemDtos = practicalItems.stream()
                .map(PracticalItemResponseDto::from)
                .collect(Collectors.toList());

        return LicenseDetailResponseDto.builder()
                .jmcd(license.getJmcd())
                .jmfldnm(license.getJmfldnm())
                .seriesnm(license.getSeriesnm())
                .qualgbnm(license.getQualgbnm())
                .summary(license.getSummary())
                .job(license.getJob())
                .career(license.getCareer())
                .trend(license.getTrend())
                .subjects(subjectDtos) // 변환된 DTO 리스트 삽입
                .practicalItems(practicalItemDtos) // 변환된 DTO 리스트 삽입
                .build();
    }
}