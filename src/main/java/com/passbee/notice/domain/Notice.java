package com.passbee.notice.domain;

import com.passbee.common.BaseTimeEntity;
import com.passbee.user.Users; // 관리자 정보를 위해 Users import
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notice")
public class Notice extends BaseTimeEntity { //

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 공지사항 작성자 (관리자)
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users author; //
}