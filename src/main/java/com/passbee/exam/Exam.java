package com.passbee.exam;

import com.passbee.license.License;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description; // 시험 설명 (예: "2024년 정기 기사 1회")

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "license_jmcd")
    private License license;

    public void setImplYy(String implYy) {
    }

    public void setExamPckd(String examPckd) {
    }

    public void setDocRegStartDt(LocalDateTime localDateTime) {
    }

    public void setDocRegEndDt(LocalDateTime localDateTime) {
    }

    public void setDocExamStartDt(LocalDateTime localDateTime) {
    }

    public void setDocPassDt(LocalDateTime localDateTime) {
    }

    public void setFee(Integer integer) {
    }

    public void setAcceptCdNm(String acceptCdNm) {
    }

    public void setEtc(String etc) {
    }

    // '시험별 리뷰' 기능은 나중에 구현하기 위해 주석 처리된 상태입니다.
    // @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL)
    // private List<Review> reviews = new ArrayList<>();
}
