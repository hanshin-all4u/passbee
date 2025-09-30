package com.all4u.all4u_server.statistics;

import com.all4u.all4u_server.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

// 2,3,4,5,9번 통계 Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "grade_stat")
public class GradeStat extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer baseYear;
    private String gradeName; // 등급명
    private String statType; // 통계 종류 (예: WRITTEN_APPLICANTS, WRITTEN_PASS_RATE)

    private String value1, value2, value3, value4, value5, value6; // 연도별 값
}
