package com.all4u.all4u_server.statistics;

import com.all4u.all4u_server.common.BaseTimeEntity;
import com.all4u.all4u_server.exam.Exam;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "statistics")
public class Statistics extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long statisticsId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @Column(nullable = false)
    private Integer round;

    @Column(nullable = false)
    private Double passRate; // 합격률
}
