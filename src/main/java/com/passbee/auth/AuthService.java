package com.passbee.auth;

import com.passbee.auth.jwt.JwtTokenProvider;
import com.passbee.auth.token.*;
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final RefreshTokenRepository refreshRepo;
    private final VerificationTokenRepository verifRepo;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    private static final long REFRESH_TTL_DAYS = 14;

    /** 로그인 결과로 내려줄 토큰 묶음 */
    public record Tokens(String accessToken, String refreshToken) {}

    /* =====================================
       신규 추가: 회원가입 / 로그인
       ===================================== */

    /** 회원가입 */
    public void register(String email, String rawPw, String name, String nickname, String phone, String address) {
        // 중복 체크
        if (usersRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 등록된 이메일입니다.");
        }
        if (usersRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 암호화 후 저장
        Users user = Users.builder()
                .email(email)
                .password(passwordEncoder.encode(rawPw))
                .name(name)
                .nickname(nickname)
                .phoneNumber(phone)
                .address(address)
                // role, emailVerified 는 엔티티 기본값 사용
                .build();

        usersRepository.save(user);
        // 이메일 인증 토큰 발급은 컨트롤러에서 createVerificationToken 호출로 처리(개발 편의상 콘솔 출력)
    }

    /** 로그인: 이메일/패스워드 검증 후 access/refresh 발급 */
    public Tokens login(String email, String rawPw) {
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(rawPw, user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        // (선택) 이메일 인증 강제
        // if (!user.isEmailVerified()) {
        //     throw new IllegalStateException("이메일 인증이 필요합니다.");
        // }

        String access = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), String.valueOf(user.getRole()));
        String refresh = createAndSaveRefreshToken(user, "login", "");

        return new Tokens(access, refresh);
    }

    /* =====================================
       기존 구현: 토큰 회전/로그아웃/검증/초기화 등
       ===================================== */

    public Optional<String> refreshAccessToken(String refreshToken) {
        return refreshRepo.findByTokenAndRevokedFalse(refreshToken).flatMap(rt -> {
            if (rt.getExpiresAt().isBefore(Instant.now())) return Optional.empty();
            Users user = rt.getUser();
            // 회전: 기존 토큰 revoke
            rt.setRevoked(true);
            refreshRepo.save(rt);
            // 새 refresh 발급(필요 시 쿠키 교체 로직과 연계 가능)
            createAndSaveRefreshToken(user, "rotation", "");
            // 새 Access 반환
            String access = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), String.valueOf(user.getRole()));
            return Optional.of(access);
        });
    }

    public void logout(String refreshToken, Users me) {
        refreshRepo.findByTokenAndRevokedFalse(refreshToken).ifPresent(rt -> {
            if (rt.getUser().getId().equals(me.getId())) {
                rt.setRevoked(true);
                refreshRepo.save(rt);
            }
        });
    }

    public String createAndSaveRefreshToken(Users user, String ua, String ip) {
        RefreshToken rt = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plus(REFRESH_TTL_DAYS, ChronoUnit.DAYS))
                .userAgent(ua)
                .ipAddress(ip)
                .build();
        refreshRepo.save(rt);
        return rt.getToken();
    }

    public VerificationToken createVerificationToken(Users user, TokenType type, long minutes) {
        VerificationToken vt = VerificationToken.builder()
                .user(user)
                .type(type)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plus(minutes, ChronoUnit.MINUTES))
                .build();
        return verifRepo.save(vt);
    }

    public boolean verifyEmail(String token) {
        return verifRepo.findByTokenAndTypeAndUsedFalse(token, TokenType.EMAIL_VERIFY)
                .filter(v -> v.getExpiresAt().isAfter(Instant.now()))
                .map(v -> {
                    v.setUsed(true); verifRepo.save(v);
                    Users u = v.getUser();
                    u.setEmailVerified(true);
                    usersRepository.save(u);
                    return true;
                }).orElse(false);
    }

    public boolean resetPassword(String token, String rawPassword) {
        return verifRepo.findByTokenAndTypeAndUsedFalse(token, TokenType.PASSWORD_RESET)
                .filter(v -> v.getExpiresAt().isAfter(Instant.now()))
                .map(v -> {
                    v.setUsed(true); verifRepo.save(v);
                    Users u = v.getUser();
                    u.setPassword(passwordEncoder.encode(rawPassword));
                    usersRepository.save(u);
                    return true;
                }).orElse(false);
    }
}