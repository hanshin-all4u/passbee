package com.passbee.exam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List; // ▼▼▼ [추가]

@Repository
public interface PracticalItemRepository extends JpaRepository<PracticalItem, Long> {
    void deleteByImplYyAndImplSeq(String implYy, String implSeq);

    // ▼▼▼ [추가] 자격증 ID(jmcd)로 실기 지참물 목록 조회
    List<PracticalItem> findByLicense_Jmcd(String jmcd);
}