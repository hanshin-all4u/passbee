package com.passbee.auth;

import com.passbee.auth.token.TokenType;
import com.passbee.mail.MailService;
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsersRepository usersRepository;
    private final MailService mailService; // ✅ 실제 메일 발송

    @GetMapping("/ping")
    public Map<String,String> ping() { return Map.of("ok","auth"); }

    /* ---------------------------
       회원가입 / 로그인
       --------------------------- */

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq req, HttpServletResponse resp) {
        // 1) 회원 생성
        authService.register(
                req.getEmail(), req.getPassword(), req.getName(),
                req.getNickname(), req.getPhoneNumber(), req.getAddress()
        );

        // 2) 이메일 인증 토큰 발급 + 메일 발송
        usersRepository.findByEmail(req.getEmail()).ifPresent(u -> {
            var vt = authService.createVerificationToken(u, TokenType.EMAIL_VERIFY, 60);
            var link = "http://localhost:8080/api/auth/verify-email?token=" + vt.getToken();
            mailService.send(u.getEmail(), "[PassBee] 이메일 인증", "아래 링크를 눌러 인증해주세요:\n" + link);
        });

        // 3) 자동 로그인 (선택)
        var tokens = authService.login(req.getEmail(), req.getPassword());
        Cookie c = new Cookie("refresh_token", tokens.refreshToken());
        c.setHttpOnly(true); c.setSecure(true); c.setPath("/");
        c.setMaxAge((int) Duration.ofDays(14).toSeconds());
        resp.addCookie(c);

        return ResponseEntity.ok(Map.of("accessToken", tokens.accessToken()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq req, HttpServletResponse resp) {
        var tokens = authService.login(req.getEmail(), req.getPassword());
        Cookie c = new Cookie("refresh_token", tokens.refreshToken());
        c.setHttpOnly(true); c.setSecure(true); c.setPath("/");
        c.setMaxAge((int) Duration.ofDays(14).toSeconds());
        resp.addCookie(c);
        return ResponseEntity.ok(Map.of("accessToken", tokens.accessToken()));
    }

    /* ---------------------------
       토큰 갱신 / 로그아웃
       --------------------------- */

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @RequestHeader(value = "X-Refresh-Token", required = false) String headerToken,
            @CookieValue(value = "refresh_token", required = false) String cookieToken,
            HttpServletResponse resp) {

        String rt = headerToken != null ? headerToken : cookieToken;
        if (rt == null) return ResponseEntity.badRequest().body(Map.of("code","BAD_REQUEST","message","Missing refresh token"));

        return authService.refreshAccessToken(rt)
                .map(access -> {
                    Cookie c = new Cookie("refresh_token", rt);
                    c.setHttpOnly(true); c.setSecure(true); c.setPath("/");
                    c.setMaxAge((int) Duration.ofDays(14).toSeconds());
                    resp.addCookie(c);
                    return ResponseEntity.ok(Map.of("accessToken", access));
                })
                .orElse(ResponseEntity.status(401).body(Map.of("code","TOKEN_INVALID","message","Invalid/expired refresh token")));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "X-Refresh-Token", required = false) String headerToken,
            @CookieValue(value = "refresh_token", required = false) String cookieToken,
            @AuthenticationPrincipal Users me, // ✅ expression 제거
            HttpServletResponse resp) {
        String rt = headerToken != null ? headerToken : cookieToken;
        if (rt != null && me != null) authService.logout(rt, me);

        Cookie c = new Cookie("refresh_token", ""); c.setPath("/"); c.setMaxAge(0); resp.addCookie(c);
        return ResponseEntity.ok(Map.of("message","logged out"));
    }

    /* ---------------------------
       이메일 인증 / 비밀번호 재설정
       --------------------------- */

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody EmailReq req) {
        usersRepository.findByEmail(req.getEmail()).ifPresent(u -> {
            var vt = authService.createVerificationToken(u, TokenType.PASSWORD_RESET, 30);
            var link = "http://localhost:8080/api/auth/reset-password?token=" + vt.getToken();
            mailService.send(u.getEmail(), "[PassBee] 비밀번호 재설정", "아래 링크에서 비밀번호를 재설정하세요:\n" + link);
        });
        return ResponseEntity.ok(Map.of("message","If the email exists, a reset link was sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetReq req) {
        boolean ok = authService.resetPassword(req.getToken(), req.getNewPassword());
        return ok ? ResponseEntity.ok(Map.of("message","password reset"))
                : ResponseEntity.badRequest().body(Map.of("code","TOKEN_INVALID","message","Invalid or expired token"));
    }

    // ✅ 브라우저에서 링크 클릭을 고려해 GET으로 열어두기
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        return authService.verifyEmail(token)
                ? ResponseEntity.ok(Map.of("message","verified"))
                : ResponseEntity.badRequest().body(Map.of("code","TOKEN_INVALID","message","Invalid or expired token"));
    }

    /* ====== DTOs ====== */
    @Data static class EmailReq { @Email @NotBlank private String email; }
    @Data static class ResetReq { @NotBlank private String token; @NotBlank private String newPassword; }
    @Data static class LoginReq { @Email @NotBlank private String email; @NotBlank private String password; }
    @Data static class RegisterReq {
        @Email @NotBlank private String email;
        @NotBlank private String password;
        @NotBlank private String name;
        @NotBlank private String nickname;
        private String phoneNumber;
        private String address;
    }
}