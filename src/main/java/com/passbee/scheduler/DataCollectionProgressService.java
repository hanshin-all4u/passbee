package com.passbee.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 데이터 수집 진행 상태 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataCollectionProgressService {

    private final DataCollectionProgressRepository progressRepository;

    /**
     * 진행 상태 조회 또는 생성
     */
    @Transactional
    public DataCollectionProgress getOrCreate(String collectionType) {
        return progressRepository.findByCollectionType(collectionType)
                .orElseGet(() -> {
                    DataCollectionProgress progress = DataCollectionProgress.builder()
                            .collectionType(collectionType)
                            .status(DataCollectionProgress.CollectionStatus.NOT_STARTED)
                            .processedItems(0)
                            .build();
                    return progressRepository.save(progress);
                });
    }

    /**
     * 수집 작업 시작
     */
    @Transactional
    public void markAsStarted(String collectionType) {
        DataCollectionProgress progress = getOrCreate(collectionType);
        progress.markAsStarted();
        progressRepository.save(progress);
        log.info("[{}] Collection started", collectionType);
    }

    /**
     * 수집 작업 완료
     */
    @Transactional
    public void markAsCompleted(String collectionType) {
        DataCollectionProgress progress = getOrCreate(collectionType);
        progress.markAsCompleted();
        progressRepository.save(progress);
        log.info("[{}] Collection completed. Total items: {}", collectionType, progress.getProcessedItems());
    }

    /**
     * 수집 작업 실패
     */
    @Transactional
    public void markAsFailed(String collectionType, String errorMessage) {
        DataCollectionProgress progress = getOrCreate(collectionType);
        progress.markAsFailed(errorMessage);
        progressRepository.save(progress);
        log.error("[{}] Collection failed: {}", collectionType, errorMessage);
    }

    /**
     * 페이지 진행 상태 업데이트
     */
    @Transactional
    public void updatePageProgress(String collectionType, int currentPage, int processedItems) {
        DataCollectionProgress progress = getOrCreate(collectionType);
        progress.setCurrentPage(currentPage);
        progress.setProcessedItems(processedItems);
        progressRepository.save(progress);
    }

    /**
     * 인덱스 진행 상태 업데이트
     */
    @Transactional
    public void updateIndexProgress(String collectionType, int currentIndex, int totalItems, int processedItems) {
        DataCollectionProgress progress = getOrCreate(collectionType);
        progress.setCurrentIndex(currentIndex);
        progress.setTotalItems(totalItems);
        progress.setProcessedItems(processedItems);
        progressRepository.save(progress);
    }

    /**
     * 통계 수집 진행 상태 업데이트
     */
    @Transactional
    public void updateStatProgress(String collectionType, int currentYear, String currentGradeCode, int processedItems) {
        DataCollectionProgress progress = getOrCreate(collectionType);
        progress.setCurrentYear(currentYear);
        progress.setCurrentGradeCode(currentGradeCode);
        progress.setProcessedItems(processedItems);
        progressRepository.save(progress);
    }

    /**
     * 실기 지참물 수집 진행 상태 업데이트
     */
    @Transactional
    public void updatePracticalProgress(String collectionType, String jmcd, String implYy, int implSeq, int processedItems) {
        DataCollectionProgress progress = getOrCreate(collectionType);
        progress.setCurrentJmcd(jmcd);
        progress.setCurrentImplYy(implYy);
        progress.setCurrentImplSeq(implSeq);
        progress.setProcessedItems(processedItems);
        progressRepository.save(progress);
    }

    /**
     * 진행 상태 초기화
     */
    @Transactional
    public void reset(String collectionType) {
        DataCollectionProgress progress = getOrCreate(collectionType);
        progress.reset();
        progressRepository.save(progress);
        log.info("[{}] Progress reset", collectionType);
    }

    /**
     * 모든 진행 상태 초기화
     */
    @Transactional
    public void resetAll() {
        progressRepository.findAll().forEach(progress -> {
            progress.reset();
            progressRepository.save(progress);
        });
        log.info("All collection progress reset");
    }

    /**
     * 현재 진행 상태 조회
     */
    @Transactional(readOnly = true)
    public DataCollectionProgress getProgress(String collectionType) {
        return progressRepository.findByCollectionType(collectionType).orElse(null);
    }

    /**
     * 진행 중인 작업이 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean hasInProgressCollection() {
        return progressRepository.existsByStatus(DataCollectionProgress.CollectionStatus.IN_PROGRESS);
    }

    /**
     * 이어서 수집할 수 있는지 확인
     */
    @Transactional(readOnly = true)
    public boolean canResume(String collectionType) {
        DataCollectionProgress progress = getProgress(collectionType);
        return progress != null && 
               progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS;
    }
}

