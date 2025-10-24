package com.passbee.license;

import org.springframework.data.domain.Page; // ▼▼▼ [추가]
import org.springframework.data.domain.Pageable; // ▼▼▼ [추가]
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenseRepository extends JpaRepository<License, String> {

    // ▼▼▼ [추가] 1. 키워드 검색 (REQ-CERT-002)
    // 자격증명(jmfldnm)에 키워드가 포함된 것을 페이징 조회
    Page<License> findByJmfldnmContaining(String keyword, Pageable pageable);

    // ▼▼▼ [추가] 2. 분야별 조회 (REQ-CERT-001)
    // 분야명(seriesnm)이 일치하는 것을 페이징 조회
    Page<License> findBySeriesnm(String seriesnm, Pageable pageable);

    // ▼▼▼ [추가] 3. 키워드 + 분야별 조회 (조합)
    Page<License> findByJmfldnmContainingAndSeriesnm(String keyword, String seriesnm, Pageable pageable);
}