package com.passbee.scheduler;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DataCollectionProgressRepository extends JpaRepository<DataCollectionProgress, Long> {
    
    /**
     * 수집 타입으로 진행 상태 조회
     */
    Optional<DataCollectionProgress> findByCollectionType(String collectionType);
    
    /**
     * 진행 중인 작업이 있는지 확인
     */
    boolean existsByStatus(DataCollectionProgress.CollectionStatus status);
}

