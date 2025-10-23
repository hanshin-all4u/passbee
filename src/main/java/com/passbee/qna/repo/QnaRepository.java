package com.passbee.qna.repo;

import com.passbee.qna.domain.Qna;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    // ▼▼▼ [수정] 메서드 이름을 findAllByOrderByCreatedAtDesc에서 findAll로 변경합니다. ▼▼▼
    // (JpaRepository가 기본 제공하는 findAll(Pageable)을 사용하게 됩니다)
    // Page<Qna> findAllByOrderByCreatedAtDesc(Pageable pageable); // <- 이 줄 삭제

    // Pageable을 사용하는 findAll은 JpaRepository에 이미 정의되어 있으므로
    // 이 파일에 추가로 메서드를 선언할 필요가 없습니다.
    // 만약 findAllByOrderByCreatedAtDesc만 있었다면, 그 선언을 삭제하고 JpaRepository만 상속받도록 둡니다.
}