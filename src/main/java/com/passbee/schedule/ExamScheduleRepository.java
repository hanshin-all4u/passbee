package com.passbee.schedule;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {

    // 월 범위 조회 (기존)
    List<ExamSchedule> findByDateBetween(LocalDate from, LocalDate to);

    // 월 범위 + jmcd 필터 (기존)
    List<ExamSchedule> findByJmcdAndDateBetween(String jmcd, LocalDate from, LocalDate to);

    // ✅ 월 범위 + 유형 in (새로 추가)
    List<ExamSchedule> findByDateBetweenAndTypeIn(LocalDate from, LocalDate to,
                                                  Collection<ExamSchedule.ScheduleType> types);

    // ✅ 월 범위 + jmcd + 유형 in (새로 추가)
    List<ExamSchedule> findByJmcdAndDateBetweenAndTypeIn(String jmcd, LocalDate from, LocalDate to,
                                                         Collection<ExamSchedule.ScheduleType> types);
}
