package com.passbee.license;

import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<License, String> 으로 변경
public interface LicenseRepository extends JpaRepository<License, String> {
    // findById와 기능이 중복되므로 이 메소드는 삭제합니다.
    // Optional<License> findByJmcd(String jmcd);
}