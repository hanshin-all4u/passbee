package com.all4u.all4u_server.agency;

import com.all4u.all4u_server.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

// 15. 자격정보 인정 기관 정보 Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "agency")
public class Agency extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agencyId;

    @Column(columnDefinition = "TEXT")
    private String agencyName; // 기관명

    private String recognitionRate; // 인정비율
}
