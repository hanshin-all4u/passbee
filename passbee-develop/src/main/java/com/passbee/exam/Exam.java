package com.passbee.exam;

import com.passbee.common.BaseTimeEntity;
import com.passbee.license.License;
import com.passbee.review.Review;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "exams")
public class Exam extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long examId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jmcd", referencedColumnName = "jmcd") // name을 jmcd로 변경
    private License license;

    private String implYy;
    private String examPckd;
    private LocalDateTime docRegStartDt;
    private LocalDateTime docRegEndDt;
    private LocalDateTime docExamStartDt;
    private LocalDateTime docPassDt;
    private Integer fee;
    private String acceptCdNm;
    private String etc;

    @OneToMany(mappedBy = "exam")
    private List<Review> reviews = new ArrayList<>();
}
