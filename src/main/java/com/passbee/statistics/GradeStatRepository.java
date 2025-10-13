package com.passbee.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GradeStatRepository extends JpaRepository<GradeStat, Long> {
    @Transactional
    void deleteByBaseYear(Integer year);

    boolean existsByBaseYearAndGradeNameAndStatType(Integer baseYear, String gradeName, String statType);
}
