package com.passbee.related;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "license_relation",
        uniqueConstraints = @UniqueConstraint(name = "uk_rel",
                columnNames = {"src_jmcd","dst_jmcd","type"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LicenseRelation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="src_jmcd", nullable=false, length=20)
    private String srcJmcd;

    @Column(name="dst_jmcd", nullable=false, length=20)
    private String dstJmcd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RelationType type;

    private BigDecimal weight;   // 0~1 가중치 (정렬용)
    private String note;

    @CreationTimestamp private LocalDateTime createdAt;
    @UpdateTimestamp private LocalDateTime updatedAt;
}
