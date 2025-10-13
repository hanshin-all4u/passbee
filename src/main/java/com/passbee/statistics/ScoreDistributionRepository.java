package com.passbee.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreDistributionRepository extends JpaRepository<ScoreDistribution, Long> {
    void deleteByBaseYear(Integer baseYear);
}

