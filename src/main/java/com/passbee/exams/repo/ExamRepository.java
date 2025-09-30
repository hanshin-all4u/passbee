package com.passbee.exams.repo;

import com.passbee.exams.domain.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    @Query("""
    select e from Exam e
    where (:q is null or :q = '' or lower(e.title) like lower(concat('%', :q, '%'))
           or lower(e.org) like lower(concat('%', :q, '%')))
    order by e.date nulls last, e.id desc
  """)
    List<Exam> search(String q);
}
