package com.passbee.license;

import lombok.Builder;
import lombok.Getter;

// 사용자에게 보여줄 정보만 담는 DTO (Data Transfer Object)
@Getter
public class LicenseResponseDto {

    private final String jmcd; // 자격증 종목코드
    private final String jmfldnm; // 자격증 이름
    private final String seriesnm; // 계열 이름
    private final String qualgbnm; // 자격 구분 (e.g., 국가기술자격)
    private final String obligfldnm; // 직무 분야

    @Builder
    public LicenseResponseDto(String jmcd, String jmfldnm, String seriesnm, String qualgbnm, String obligfldnm) {
        this.jmcd = jmcd;
        this.jmfldnm = jmfldnm;
        this.seriesnm = seriesnm;
        this.qualgbnm = qualgbnm;
        this.obligfldnm = obligfldnm;
    }

    // License(Entity)를 LicenseResponseDto로 변환하는 메소드
    public static LicenseResponseDto fromEntity(License license) {
        return LicenseResponseDto.builder()
                .jmcd(license.getJmcd())
                .jmfldnm(license.getJmfldnm())
                .seriesnm(license.getSeriesnm())
                .qualgbnm(license.getQualgbnm())
                .obligfldnm(license.getObligfldnm())
                .build();
    }
}
