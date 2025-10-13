package com.passbee.license;

import lombok.Builder;
import lombok.Getter;

// 자격증의 '상세 정보'를 사용자에게 보여주기 위한 DTO
@Getter
public class LicenseDetailResponseDto {

    // --- 기본 정보 ---
    private final String jmcd; // 자격증 종목코드
    private final String jmfldnm; // 자격증 이름
    private final String seriesnm; // 계열 이름
    private final String qualgbnm; // 자격 구분
    private final String obligfldnm; // 직무 분야

    // --- 상세 정보 ---
    private final String summary; // 개요
    private final String job; // 수행직무
    private final String career; // 진로 및 전망

    @Builder
    public LicenseDetailResponseDto(String jmcd, String jmfldnm, String seriesnm, String qualgbnm, String obligfldnm, String summary, String job, String career) {
        this.jmcd = jmcd;
        this.jmfldnm = jmfldnm;
        this.seriesnm = seriesnm;
        this.qualgbnm = qualgbnm;
        this.obligfldnm = obligfldnm;
        this.summary = summary;
        this.job = job;
        this.career = career;
    }

    // License(Entity)를 LicenseDetailResponseDto로 변환하는 정적 메소드
    public static LicenseDetailResponseDto fromEntity(License license) {
        return LicenseDetailResponseDto.builder()
                .jmcd(license.getJmcd())
                .jmfldnm(license.getJmfldnm())
                .seriesnm(license.getSeriesnm())
                .qualgbnm(license.getQualgbnm())
                .obligfldnm(license.getObligfldnm())
                .summary(license.getSummary())
                .job(license.getJob())
                .career(license.getCareer())
                .build();
    }
}
