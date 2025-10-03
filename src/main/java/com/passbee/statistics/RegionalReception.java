package com.passbee.statistics;

import com.passbee.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

// 16. 거주지별 접수 현황 Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "regional_reception")
public class RegionalReception extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String residence; // 거주지
    private String receptionBranch; // 접수지사
    private String examName; // 시험명
    private String licenseName; // 종목명
    private Integer receptionCount; // 접수자 수
    private Integer round; // 차수
    private Integer baseYear;
    private String gradeName;
}
