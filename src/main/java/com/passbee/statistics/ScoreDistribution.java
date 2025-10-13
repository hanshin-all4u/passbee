package com.passbee.statistics;

import com.passbee.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * 연도별 회별 국가기술자격시험 원서 점수 분포
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "score_distributions")
public class ScoreDistribution extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer baseYear;       // 기준 년도
    private String gradeName;       // 등급명 (grdNm)
    private String implSeq;         // 시행회차
    private String examTypeName;    // 시험구분명 (comCdNm - 필기/실기)
    private String licenseName;     // 자격증명 (jmFldNm)
    private String subjectName;     // 과목명 (kmNm)
    
    // 점수 구간별 인원 (40점 이하, 50점, 60점, 70점, 80점, 81점 이상)
    private Integer score40;        // 40점 이하
    private Integer score50;        // 50점대
    private Integer score60;        // 60점대
    private Integer score70;        // 70점대
    private Integer score80;        // 80점대
    private Integer score81;        // 81점 이상
    private Double scoreAvg;        // 평균 점수
}

