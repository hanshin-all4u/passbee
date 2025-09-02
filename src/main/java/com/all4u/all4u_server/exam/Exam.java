package com.all4u.all4u_server.exam;

import com.all4u.all4u_server.common.BaseTimeEntity;
import com.all4u.all4u_server.license.License;
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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer examId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "license_id")
    private License license;

    @Column(nullable = false)
    private Integer round;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExamType examType;

    private LocalDateTime examDate;

    private LocalDateTime receptionStart;
    private LocalDateTime receptionEnd;

    private Integer writtenFee;     // 필기 응시료
    private Integer practicalFee;   // 실기 응시료

    private LocalDateTime writtenExamDate;
    private LocalDateTime practicalExamDate;
    private LocalDateTime resultDate;

    @Column(length = 200)
    private String receptionDesk;

    @Column(columnDefinition = "text")
    private String notes;

    @OneToMany(mappedBy = "exam")
    private List<com.all4u.all4u_server.review.Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "exam")
    private List<com.all4u.all4u_server.statistics.Statistics> statistics = new ArrayList<>();
}
