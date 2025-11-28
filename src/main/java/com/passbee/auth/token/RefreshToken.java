package com.passbee.auth.token;

import com.passbee.user.Users;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RefreshToken {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiresAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean revoked = false;

    @Builder.Default
    @Column(nullable = false)
    private Instant issuedAt = Instant.now();

    private String userAgent;
    private String ipAddress;
}