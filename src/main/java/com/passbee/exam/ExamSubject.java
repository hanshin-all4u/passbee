package com.passbee.exam;

import com.passbee.common.BaseTimeEntity;
import com.passbee.license.License;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "exam_subjects")
public class ExamSubject extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jmcd", referencedColumnName = "jmcd")
    private License license;

    private String jmNm; // 종목명
    private String kmNm; // 과목명
    private String kmYn; // 필수과목여부
}


