package com.passbee.config;

import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        createTestUser();
    }

    private void createTestUser() {
        try {
            String email = "test@example.com";
            if (!usersRepository.existsByEmail(email)) {
                Users u = Users.builder()
                        .name("테스트사용자")
                        .nickname("테스트닉네임")
                        .email(email)
                        .password(passwordEncoder.encode("pass1234"))
                        .emailVerified(true)
                        // .role(Role.USER)  // ← enum/문자열 타입에 맞게 추가
                        .build();
                usersRepository.save(u);
            }
        } catch (Exception ignored) {}
    }
}