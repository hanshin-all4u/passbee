package com.passbee.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    boolean existsByEmail(String email);
    Optional<Users> findByEmail(String email);

    // ▼▼▼ [추가] 닉네임 중복 확인 메서드 ▼▼▼
    boolean existsByNickname(String nickname);
}