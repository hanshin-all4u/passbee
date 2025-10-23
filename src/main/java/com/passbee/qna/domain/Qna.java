package com.passbee.qna.domain;

import com.passbee.common.BaseTimeEntity;
import com.passbee.user.Users;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "qna")
public class Qna extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user; // 질문 작성자

    // ▼▼▼ [추가] 비밀글 여부 (true = 비밀글) ▼▼▼
    @Column(nullable = false)
    @Builder.Default
    private boolean secret = false;
}