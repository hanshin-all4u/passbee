package com.passbee.mypage;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_license",
        uniqueConstraints = @UniqueConstraint(name="uk_user_license", columnNames={"user_id","jmcd","obtained_date"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserLicense {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(length=32, nullable=false)
    private String jmcd;

    @Column(name="obtained_date", nullable=false)
    private LocalDate obtainedDate;

    @Column(name="level_grade", length=32)
    private String levelGrade;

    @Column(precision=5, scale=2)
    private java.math.BigDecimal score;

    @Column(name="certificate_no", length=64)
    private String certificateNo;

    @Column(length=64)
    private String issuer;

    private LocalDate expiresAt;

    @Column(length=255)
    private String memo;
}