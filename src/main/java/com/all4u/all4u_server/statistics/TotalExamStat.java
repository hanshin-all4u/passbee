package com.all4u.all4u_server.statistics;

import com.all4u.all4u_server.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

// 1. 연도별 응시자/합격자 수 Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "total_exam_stat")
public class TotalExamStat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer baseYear; // 기준년도

    // 필기 응시
    private Long writtenApplicants1, writtenApplicants2, writtenApplicants3, writtenApplicants4, writtenApplicants5, writtenApplicants6;
    // 실기 응시
    private Long practicalApplicants1, practicalApplicants2, practicalApplicants3, practicalApplicants4, practicalApplicants5, practicalApplicants6;
    // 필기 합격
    private Long writtenPassers1, writtenPassers2, writtenPassers3, writtenPassers4, writtenPassers5, writtenPassers6;
    // 실기 합격
    private Long practicalPassers1, practicalPassers2, practicalPassers3, practicalPassers4, practicalPassers5, practicalPassers6;
}
