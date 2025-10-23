package com.passbee.review;

import com.passbee.common.BaseTimeEntity;
import com.passbee.license.License;
import com.passbee.user.Users;
import com.passbee.attachment.AttachmentFile;
import com.passbee.comment.domain.Comment; // ▼▼▼ [추가] import
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "review")
public class Review extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "jmcd", referencedColumnName = "jmcd")
    private License license;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Difficulty difficulty;

    @Column(columnDefinition = "text")
    private String comment;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AttachmentFile> files = new ArrayList<>();

    // ▼▼▼ [추가] 댓글 목록 (Review가 삭제되면 댓글도 함께 삭제) ▼▼▼
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}