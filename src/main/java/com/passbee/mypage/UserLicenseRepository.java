package com.passbee.mypage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserLicenseRepository extends JpaRepository<UserLicense, Long> {
    List<UserLicense> findByUserIdOrderByObtainedDateDesc(Long userId);
    Optional<UserLicense> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndJmcdAndObtainedDate(Long userId, String jmcd, java.time.LocalDate obtainedDate);
}