package com.all4u.all4u_server.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface GradeStatRepository extends JpaRepository<GradeStat, Long> {
    @Transactional
    void deleteByBaseYear(Integer year);
}
