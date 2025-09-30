package com.passbee.exams.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name="exam")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Exam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String title;

    private LocalDate date;
    private String org;

    @Column(length=1000)
    private String link;

    @Column(length=2000)
    private String description;

    @Column(nullable=false, updatable=false)
    private LocalDateTime createdAt;

    @PrePersist void onCreate(){ this.createdAt = LocalDateTime.now(); }
}
