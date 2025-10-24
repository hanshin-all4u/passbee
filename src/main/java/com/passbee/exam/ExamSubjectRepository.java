package com.passbee.exam;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; // ▼▼▼ [추가]

public interface ExamSubjectRepository extends JpaRepository<ExamSubject, Long> {

    // ▼▼▼ [추가] 자격증 ID(jmcd)로 시험 과목 목록 조회
    List<ExamSubject> findByLicense_Jmcd(String jmcd);
}