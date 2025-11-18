package com.passbee.schedule;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "exam_schedule",
        uniqueConstraints = @UniqueConstraint(name="uk_exam_schedule_jmcd_date_type", columnNames={"jmcd","date","type"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExamSchedule {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length=32, nullable=false)
    private String jmcd;

    @Column(nullable=false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=16)
    private ScheduleType type;

    @Column(name="apply_url", length=512)
    private String applyUrl;

    @Column(length=255)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    @Builder.Default
    private Source source = Source.QNET;

    public enum ScheduleType { REG_OPEN, REG_CLOSE, PI_EXAM, SI_EXAM, RESULT }
    public enum Source { QNET, MANUAL }
}
