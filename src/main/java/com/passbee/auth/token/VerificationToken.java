package com.passbee.auth.token;

import com.passbee.user.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "verification_token")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VerificationToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private TokenType type;

    @Column(nullable = false)
    private Instant expiresAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean used = false;

    @Builder.Default
    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}