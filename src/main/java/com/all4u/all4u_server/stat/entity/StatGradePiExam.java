package com.all4u.all4u_server.stat.entity;
import jakarta.persistence.*; import lombok.*;

@Entity @Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class StatGradePiExam {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int baseYear;
    private String gradeName;

    private Long y1, y2, y3, y4, y5, y6; // 기준년도~과거
}