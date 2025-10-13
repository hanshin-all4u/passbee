package com.passbee.agency;

import com.passbee.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

// 15. 자격정보 인정 기관 정보 Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "agency")
public class Agency extends BaseTimeEntity {

    @Id
    private String rcogInstiCd; // PK로 변경

    @Column(columnDefinition = "TEXT")
    private String rcogInstiNm; // 기관명 (API 명세 맞춤)

    private String crerRcogRate; // 경력인정 비율 (API 명세 맞춤)

    // API 명세 추가 필드: 유효기간 시작/종료일 (yyyyMMdd)
    private String validTermStartDt;
    private String validTermEndDt;
}