package com.passbee.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByEmail(String email);

    // ✅ 엔티티 필드명은 id (DB 컬럼 user_id와 매핑)
    @Query("select u.id from Users u where u.email = :email")
    Optional<Long> findIdByEmail(String email);

    // ✅ 이전에 없어서 컴파일 에러 났던 메서드들
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}