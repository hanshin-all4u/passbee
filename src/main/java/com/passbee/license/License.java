package com.passbee.license;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.passbee.common.BaseTimeEntity;
import com.passbee.review.Review; // Review의 집 주소 import
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "license")
public class License extends BaseTimeEntity {

    @Id
    private String jmcd;

    // --- (기존 필드들은 그대로 유지) ---
    private String jmfldnm;
    private String seriescd;
    private String seriesnm;
    private String qualgbcd;
    private String qualgbnm;
    private String mdobligfldcd;
    private String mdobligfldnm;
    private String obligfldcd;
    private String obligfldnm;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(columnDefinition = "TEXT")
    private String job;

    @Column(columnDefinition = "TEXT")
    private String career;

    // ↓↓↓ '자격증'과 '리뷰'의 관계를 정의합니다. (이 관계의 주인) ↓↓↓
    @JsonManagedReference("license-review") // 무한 루프 방지를 위한 이름표
    @OneToMany(mappedBy = "license", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
}