package com.all4u.all4u_server.license;

import com.all4u.all4u_server.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "license")
public class License extends BaseTimeEntity {

    @Id
    private String jmcd;

    // --- 자격 종목 목록 (qualifications) ---
    private String jmfldnm;
    private String seriescd;
    private String seriesnm;
    private String qualgbcd;
    private String qualgbnm;
    private String mdobligfldcd;
    private String mdobligfldnm;
    private String obligfldcd;
    private String obligfldnm;

    // --- 자격 종목 상세 정보 (qualitative-info) ---
    @Column(columnDefinition = "TEXT")
    private String summary; // 개요

    @Column(columnDefinition = "TEXT")
    private String job; // 수행직무

    @Column(columnDefinition = "TEXT")
    private String career; // 진로 및 전망
}