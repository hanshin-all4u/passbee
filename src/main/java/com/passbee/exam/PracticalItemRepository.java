package com.passbee.exam;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticalItemRepository extends JpaRepository<PracticalItem, Long> {
    void deleteByImplYyAndImplSeq(String implYy, String implSeq);
}

