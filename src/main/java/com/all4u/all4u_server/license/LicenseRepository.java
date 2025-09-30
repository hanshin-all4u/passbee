package com.all4u.all4u_server.license;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<License, String> 으로 변경
public interface LicenseRepository extends JpaRepository<License, String> {
    // findById와 기능이 중복되므로 이 메소드는 삭제합니다.
    // Optional<License> findByJmcd(String jmcd);
}