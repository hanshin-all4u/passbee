package com.passbee.review;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.passbee.common.BaseTimeEntity;
import com.passbee.license.License;
import com.passbee.user.Users;
import com.passbee.exam.Exam;               // ★ exam_id가 NOT NULL이므로 매핑 추가 권장
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "review")
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")            // ★ DB의 PK와 동일하게
    private Long reviewId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(nullable = false)
    private int rating;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @JsonBackReference("license-review")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_jmcd", nullable = false) // ★ DB와 동일
    private License license;

    @JsonBackReference("user-review")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)      // ★ DB와 동일
    private Users user;

    // ★ review 테이블에는 exam_id NOT NULL FK가 있으므로 엔티티에도 추가하는 게 안전
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)      // ★ DB와 동일
    private Exam exam;

    @Builder
    public Review(String comment, int rating, Difficulty difficulty,
                  License license, Users user, Exam exam) {
        this.comment = comment;
        this.rating = rating;
        this.difficulty = difficulty;
        this.license = license;
        this.user = user;
        this.exam = exam;
    }

    public enum Difficulty {
        EASY, NORMAL, HARD
    }
}
