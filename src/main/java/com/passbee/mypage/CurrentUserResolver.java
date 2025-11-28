package com.passbee.mypage;

import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserResolver {

    private final UsersRepository usersRepository;

    public Long currentUserIdOrNull() {
        var ctx = org.springframework.security.core.context.SecurityContextHolder.getContext();
        var auth = ctx != null ? ctx.getAuthentication() : null;

        if (auth == null || !auth.isAuthenticated()) {
            return null; // 비로그인
        }
        String email = auth.getName(); // JwtAuthenticationFilter에서 username=email 로 세팅했다고 가정
        return usersRepository.findIdByEmail(email).orElse(null); // 유저 없으면 null
    }
}