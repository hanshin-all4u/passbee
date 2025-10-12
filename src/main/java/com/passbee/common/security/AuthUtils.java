package com.passbee.common.security;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class AuthUtils {
    private AuthUtils() {}

    public static Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new AccessDeniedException("Unauthenticated");
        }
        Object principal = auth.getPrincipal();

        // 1) CustomUserDetails 에 getId() 있는 경우
        try {
            var method = principal.getClass().getMethod("getId");
            Object id = method.invoke(principal);
            if (id instanceof Long l) return l;
            if (id instanceof Integer i) return i.longValue();
        } catch (Exception ignore) {}

        // 2) JWT 기반 인증에서 claim("id")로 가져오는 경우 (예: JwtAuthenticationToken)
        try {
            var method = auth.getClass().getMethod("getTokenAttributes");
            var claims = (java.util.Map<?, ?>) method.invoke(auth);
            Object id = claims.get("id");
            if (id instanceof Number n) return n.longValue();
            if (id instanceof String s) return Long.valueOf(s);
        } catch (Exception ignore) {}

        throw new AccessDeniedException("Cannot resolve current user id");
    }
}
