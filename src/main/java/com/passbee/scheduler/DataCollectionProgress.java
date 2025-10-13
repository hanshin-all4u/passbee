package com.passbee.scheduler;

import com.passbee.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 데이터 수집 진행 상태를 저장하는 엔티티
 * 애플리케이션 재시작 시 중단된 곳부터 이어서 수집하기 위해 사용
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "data_collection_progress")
public class DataCollectionProgress extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 수집 작업 타입
     * QUALIFICATIONS, QUALIFICATIONS_TO_QUALIFICATION, LICENSE_DETAILS, 
     * EXAM_SUBJECTS, PRACTICAL_ITEMS, AGENCIES, STATISTICS, SCORE_DISTRIBUTIONS
     */
    @Column(nullable = false, unique = true, length = 50)
    private String collectionType;

    /**
     * 현재 수집 상태
     * NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED
     */
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CollectionStatus status;

    /**
     * 현재 처리 중인 페이지 번호 (페이지 기반 수집용)
     */
    @Column
    private Integer currentPage;

    /**
     * 현재 처리 중인 인덱스 (리스트 기반 수집용)
     */
    @Column
    private Integer currentIndex;

    /**
     * 현재 처리 중인 연도 (통계 수집용)
     */
    @Column
    private Integer currentYear;

    /**
     * 현재 처리 중인 등급 코드 (통계 수집용)
     */
    @Column(length = 10)
    private String currentGradeCode;

    /**
     * 현재 처리 중인 자격증 코드 (시험 과목/실기 지참물 수집용)
     */
    @Column(length = 10)
    private String currentJmcd;

    /**
     * 현재 처리 중인 실행 연도 (실기 지참물 수집용)
     */
    @Column(length = 4)
    private String currentImplYy;

    /**
     * 현재 처리 중인 회차 (실기 지참물 수집용)
     */
    @Column
    private Integer currentImplSeq;

    /**
     * 총 처리할 항목 수
     */
    @Column
    private Integer totalItems;

    /**
     * 현재까지 처리된 항목 수
     */
    @Column
    private Integer processedItems;

    /**
     * 마지막 처리 시작 시간
     */
    @Column
    private LocalDateTime lastStartedAt;

    /**
     * 마지막 처리 완료 시간
     */
    @Column
    private LocalDateTime lastCompletedAt;

    /**
     * 실패 시 에러 메시지
     */
    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    /**
     * 추가 메타데이터 (JSON 형식)
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;

    /**
     * 진행률 계산 (0-100)
     */
    public Double getProgressPercentage() {
        if (totalItems == null || totalItems == 0) {
            return 0.0;
        }
        return (processedItems != null ? processedItems : 0) * 100.0 / totalItems;
    }

    /**
     * 수집 작업 시작 시 호출
     */
    public void markAsStarted() {
        this.status = CollectionStatus.IN_PROGRESS;
        this.lastStartedAt = LocalDateTime.now();
        this.errorMessage = null;
    }

    /**
     * 수집 작업 완료 시 호출
     */
    public void markAsCompleted() {
        this.status = CollectionStatus.COMPLETED;
        this.lastCompletedAt = LocalDateTime.now();
        this.errorMessage = null;
    }

    /**
     * 수집 작업 실패 시 호출
     */
    public void markAsFailed(String errorMessage) {
        this.status = CollectionStatus.FAILED;
        this.errorMessage = errorMessage;
    }

    /**
     * 진행 상태 초기화 (새로 시작할 때)
     */
    public void reset() {
        this.status = CollectionStatus.NOT_STARTED;
        this.currentPage = null;
        this.currentIndex = null;
        this.currentYear = null;
        this.currentGradeCode = null;
        this.currentJmcd = null;
        this.currentImplYy = null;
        this.currentImplSeq = null;
        this.processedItems = 0;
        this.lastStartedAt = null;
        this.lastCompletedAt = null;
        this.errorMessage = null;
    }

    /**
     * 수집 상태 열거형
     */
    public enum CollectionStatus {
        NOT_STARTED,  // 아직 시작하지 않음
        IN_PROGRESS,  // 진행 중
        COMPLETED,    // 완료
        FAILED        // 실패
    }
}

