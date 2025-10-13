package com.passbee.scheduler;

import com.passbee.qnet.service.QnetDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 데이터 수집 관리 컨트롤러 (관리자용)
 */
@Slf4j
@Tag(name = "Data Collection (Admin)", description = "데이터 수집 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/collect")
public class DataCollectionController {

    private final ScheduledDataCollector scheduledDataCollector;
    private final QnetDataService qnetDataService;

    @Operation(summary = "전체 데이터 수집 실행", description = "모든 데이터 수집 작업을 실행합니다 (시간이 오래 걸릴 수 있습니다)")
    @PostMapping("/all")
    public String collectAll() {
        log.info("Manual trigger: collectAllData");
        try {
            scheduledDataCollector.collectAllData();
            return "✅ All data collection completed successfully";
        } catch (Exception e) {
            log.error("Data collection failed", e);
            return "❌ Data collection failed: " + e.getMessage();
        }
    }

    @Operation(summary = "자격증 목록 수집 (License 테이블)")
    @PostMapping("/qualifications")
    public String collectQualifications() {
        log.info("Manual trigger: collectQualifications");
        try {
            scheduledDataCollector.collectQualifications();
            return "✅ Qualifications collected successfully";
        } catch (Exception e) {
            log.error("Qualifications collection failed", e);
            return "❌ Failed: " + e.getMessage();
        }
    }

    @Operation(summary = "자격증 목록 수집 (Qualification 테이블)")
    @PostMapping("/qualifications-to-qualification-table")
    public String collectQualificationsToQualificationTable() {
        log.info("Manual trigger: collectQualificationsToQualificationTable");
        try {
            scheduledDataCollector.collectQualificationsToQualificationTable();
            return "✅ Qualifications collected to Qualification table successfully";
        } catch (Exception e) {
            log.error("Qualifications to Qualification table collection failed", e);
            return "❌ Failed: " + e.getMessage();
        }
    }

    @Operation(summary = "자격증 상세 정보 수집")
    @PostMapping("/license-details")
    public String enrichLicenseDetails() {
        log.info("Manual trigger: enrichLicenseDetails");
        try {
            scheduledDataCollector.enrichLicenseDetails();
            return "✅ License details enriched successfully";
        } catch (Exception e) {
            log.error("License details enrichment failed", e);
            return "❌ Failed: " + e.getMessage();
        }
    }

    @Operation(summary = "시험 과목 수집", description = "모든 자격증의 시험 과목을 수집합니다")
    @PostMapping("/exam-subjects")
    public String collectExamSubjects() {
        log.info("Manual trigger: collectExamSubjects");
        try {
            scheduledDataCollector.collectExamSubjects();
            return "✅ Exam subjects collected successfully";
        } catch (Exception e) {
            log.error("Exam subjects collection failed", e);
            return "❌ Failed: " + e.getMessage();
        }
    }

    @Operation(summary = "실기시험 지참물 수집")
    @PostMapping("/practical-items")
    public String collectPracticalItems() {
        log.info("Manual trigger: collectPracticalItems");
        try {
            scheduledDataCollector.collectPracticalItems();
            return "✅ Practical items collected successfully";
        } catch (Exception e) {
            log.error("Practical items collection failed", e);
            return "❌ Failed: " + e.getMessage();
        }
    }

    @Operation(summary = "기관 정보 수집")
    @PostMapping("/agencies")
    public String collectAgencies() {
        log.info("Manual trigger: collectAgencies");
        try {
            scheduledDataCollector.collectAgencies();
            return "✅ Agencies collected successfully";
        } catch (Exception e) {
            log.error("Agencies collection failed", e);
            return "❌ Failed: " + e.getMessage();
        }
    }

    @Operation(summary = "통계 정보 수집")
    @PostMapping("/statistics")
    public String collectStatistics() {
        log.info("Manual trigger: collectStatistics");
        try {
            scheduledDataCollector.collectStatistics();
            return "✅ Statistics collected successfully";
        } catch (Exception e) {
            log.error("Statistics collection failed", e);
            return "❌ Failed: " + e.getMessage();
        }
    }

    @Operation(summary = "점수 분포 수집")
    @PostMapping("/score-distributions")
    public String collectScoreDistributions() {
        log.info("Manual trigger: collectScoreDistributions");
        try {
            scheduledDataCollector.collectScoreDistributions();
            return "✅ Score distributions collected successfully";
        } catch (Exception e) {
            log.error("Score distributions collection failed", e);
            return "❌ Failed: " + e.getMessage();
        }
    }

    // ===== 테스트/디버깅 API =====

    @Operation(summary = "[테스트] 특정 자격증의 시험 과목 API 응답 확인")
    @GetMapping("/test/exam-subjects")
    public Object testExamSubjectsApi(@RequestParam(defaultValue = "1160") String jmcd) {
        log.info("Test API call: exam-subjects for jmcd={}", jmcd);
        try {
            var result = qnetDataService.getExamSubjects(jmcd);
            return result.isEmpty() ? "⚠️ No data returned from Q-Net API" : result;
        } catch (Exception e) {
            log.error("Test API failed", e);
            return "❌ Failed: " + e.getMessage();
        }
    }

    @Operation(summary = "[테스트] 실기시험 지참물 API 응답 확인")
    @GetMapping("/test/practical-items")
    public Object testPracticalItemsApi(@RequestParam(defaultValue = "1160") String jmcd,
                                        @RequestParam(defaultValue = "2024") String implYy,
                                        @RequestParam(defaultValue = "1") String implSeq) {
        log.info("Test API call: practical-items for jmcd={}, year={}, seq={}", jmcd, implYy, implSeq);
        try {
            var result = qnetDataService.getPracticalItems(jmcd, implYy, implSeq);
            return result.isEmpty() ? "⚠️ No data returned from Q-Net API" : result;
        } catch (Exception e) {
            log.error("Test API failed", e);
            return "❌ Failed: " + e.getMessage();
        }
    }
}

