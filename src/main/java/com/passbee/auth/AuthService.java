package com.passbee.auth;

import com.passbee.auth.dto.LoginRequest;
import com.passbee.auth.dto.SignupRequest;
import com.passbee.auth.jwt.JwtTokenProvider; // JwtTokenProvider 임포트
import com.passbee.user.Role;
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider 주입

    @Transactional
    public Users signup(SignupRequest req) {
        if (usersRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
        }
        String encodedPassword = passwordEncoder.encode(req.password());
        Users newUser = Users.builder().name(req.name()).email(req.email()).password(encodedPassword).role(Role.USER).build();
        return usersRepository.save(newUser);
    }

    // ↓↓↓ login 메소드의 반환 타입을 String으로 변경하고, 내용을 수정합니다. ↓↓↓
    @Transactional(readOnly = true)
    public String login(LoginRequest req) {
        System.out.println("로그인 시도 - 이메일: " + req.email());

        Users user = usersRepository.findByEmail(req.email())
                .orElseThrow(() -> {
                    System.out.println("사용자를 찾을 수 없음: " + req.email());
                    return new BadCredentialsException("이메일 또는 비밀번호가 일치하지 않습니다.");
                });

        System.out.println("사용자 찾음: " + user.getName() + " (" + user.getEmail() + ")");

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            System.out.println("비밀번호 불일치");
            throw new BadCredentialsException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        System.out.println("인증 성공 - JWT 토큰 생성");
        // 인증 성공 시, JwtTokenProvider를 사용해 실제 토큰(String)을 생성하여 반환합니다.
        return jwtTokenProvider.createToken(user);
    }
}