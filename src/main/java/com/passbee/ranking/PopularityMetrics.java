package com.passbee.ranking;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "popularity_metrics",
        uniqueConstraints = @UniqueConstraint(name="uk_pm", columnNames = {"jmcd","period"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PopularityMetrics {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String jmcd;

    @Column(nullable = false)
    private LocalDate period;  // 해당 월 1일 (yyyy-MM-01)

    private Integer applications;        // 접수 인원
    private BigDecimal passRate;         // 0~1
    private Integer favorites;           // 즐겨찾기 수(누적)
    private Integer reviews;             // 후기 수(당월)

    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;
}