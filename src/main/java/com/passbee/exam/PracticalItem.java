package com.passbee.exam;

import com.passbee.common.BaseTimeEntity;
import com.passbee.license.License;
import jakarta.persistence.*;
import lombok.*;

/**
 * 실기시험 지참물 정보
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "practical_items")
public class PracticalItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jmcd", referencedColumnName = "jmcd")
    private License license;

    private String implYy;     // 시행연도
    private String implSeq;    // 시행회차
    private String jmNm;       // 종목명
    private String mtrlNm;     // 지참물 명
    
    @Column(length = 1000)
    private String mtrlExpl;   // 규격 (긴 텍스트일 수 있음)
}

