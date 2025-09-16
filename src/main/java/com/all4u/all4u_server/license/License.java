package com.all4u.all4u_server.license;

import com.all4u.all4u_server.common.BaseTimeEntity;
import com.all4u.all4u_server.exam.Exam;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "license")
public class License extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer licenseId;

    // Q-Net API의 자격 종목 코드
    private String jmcd;

    @Column(nullable = false, length = 100)
    private String name;

    // 분류/계열 등
    @Column(length = 50)
    private String field;

    @Column(length = 50)
    private String category;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 100)
    private String agency;

    // Qualification 엔티티에 있는 필드 추가
    private String jmfldnm;
    private String seriescd;
    private String seriesnm;
    private String qualgbcd;
    private String qualgbnm;
    private String mdobligfldcd;
    private String mdobligfldnm;
    private String obligfldcd;
    private String obligfldnm;

    @OneToMany(mappedBy = "license")
    private List<com.all4u.all4u_server.exam.Exam> exams = new ArrayList<>();
}
