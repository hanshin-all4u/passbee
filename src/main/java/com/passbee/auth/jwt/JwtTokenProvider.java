package com.passbee.auth.jwt;

import com.passbee.user.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long validityInMilliseconds;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.expiration-ms}") long validityInMilliseconds) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.validityInMilliseconds = validityInMilliseconds;
    }

    // 사용자 정보를 받아 JWT를 생성하는 메소드
    public String createToken(Users user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        // JJWT 0.12.x에서는 Claims가 불변이므로, builder에서 직접 claim을 추가합니다.
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("name", user.getName())
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }
}