package com.passbee.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface RegionalReceptionRepository extends JpaRepository<RegionalReception, Long> {
    @Transactional
    void deleteByBaseYear(Integer year);
}