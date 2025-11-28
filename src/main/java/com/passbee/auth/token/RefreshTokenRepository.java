package com.passbee.auth.token;

import com.passbee.user.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);
    void deleteAllByUser(Users user);
}