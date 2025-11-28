package com.passbee.auth.jwt;

import com.passbee.user.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private final SecretKey accessKey;
    private final long accessTtlMillis;

    public JwtTokenProvider(
            @Value("${jwt.access-secret}") String accessSecret,
            @Value("${jwt.access-ttl-ms:900000}") long accessTtlMillis
    ) {
        this.accessKey = Keys.hmacShaKeyFor(accessSecret.getBytes(StandardCharsets.UTF_8)); // 32+ bytes
        this.accessTtlMillis = accessTtlMillis;
    }

    public String createAccessToken(Long userId, String email, String role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(new Date(System.currentTimeMillis() + accessTtlMillis))
                .claims(Map.of("email", email, "role", role))
                .signWith(accessKey, Jwts.SIG.HS256)
                .compact();
    }

    public Jws<Claims> parseAccessToken(String token) {
        return Jwts.parser()
                .verifyWith(accessKey)
                .build()
                .parseSignedClaims(token);
    }

    // ===== 호환 메서드들 (기존 코드와 연결) =====
    public boolean validateToken(String token) {
        try {
            parseAccessToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    public SecretKey getKey() { return accessKey; }
    public String createToken(Users user) {
        String role = String.valueOf(user.getRole());
        return createAccessToken(user.getId(), user.getEmail(), role);
    }
}