package com.passbee.ranking;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PopularityMetricsRepository extends JpaRepository<PopularityMetrics, Long> {

    List<PopularityMetrics> findByPeriod(LocalDate period, Pageable pageable);
}
