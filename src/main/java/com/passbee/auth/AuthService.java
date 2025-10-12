package com.passbee.auth;

import com.passbee.auth.dto.AuthResponse;
import com.passbee.auth.dto.LoginRequest;
import com.passbee.auth.dto.SignupRequest;
import com.passbee.auth.jwt.JwtTokenProvider;
import com.passbee.user.Role;
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public void signup(SignupRequest req) {
        if (usersRepository.existsByEmail(req.email()))
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");

        if (usersRepository.existsByNickname(req.nickname()))
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");

        var user = Users.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))
                .nickname(req.nickname())
                .role(Role.USER)
                .build();

        usersRepository.save(user);
    }

    public AuthResponse login(LoginRequest req) {
        var authToken = new UsernamePasswordAuthenticationToken(req.email(), req.password());
        authenticationManager.authenticate(authToken); // 비번 검증

        var user = usersRepository.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String jwt = jwtTokenProvider.createToken(user);
        return new AuthResponse(jwt, jwtTokenProvider.getValidityInMilliseconds());
    }
}