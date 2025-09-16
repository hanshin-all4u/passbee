package com.all4u.all4u_server.scheduler;

import com.all4u.all4u_server.exam.service.ExamService;
import com.all4u.all4u_server.qnet.service.QualificationService;
import com.all4u.all4u_server.qnet.service.StatGradeService;
import com.all4u.all4u_server.qnet.service.StatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledDataCollector {

    // 필요한 서비스들을 주입받습니다.
    private final QualificationService qualificationService;
    private final StatService statService;
    private final StatGradeService statGradeService;
    private final ExamService examService;

    // 매일 자정(0시 0분 0초)에 실행되도록 설정합니다.
    @Scheduled(cron = "0 0 0 * * *")
    public void collectAllData() {
        log.info("Starting scheduled data collection...");

        try {
            // 1. 자격 종목 전체 데이터 수집 (총 185 페이지)
            // Q-Net API의 모든 페이지를 순회하며 데이터를 가져옵니다.
            IntStream.rangeClosed(1, 185).forEach(page -> {
                try {
                    int saved = qualificationService.importPage(page, 100);
                    log.info("Qualification data collected: {} items on page {}", saved, page);
                } catch (Exception e) {
                    log.error("Failed to collect qualification data on page {}: {}", page, e.getMessage());
                }
            });

            // 2. 통계 데이터 수집 (2024년 기준)
            int baseYear = 2024;
            try {
                statService.importTotal(baseYear);
                log.info("Total statistics data collected for year {}", baseYear);
            } catch (Exception e) {
                log.error("Failed to collect total statistics data: {}", e.getMessage());
            }

            try {
                statGradeService.importGradPiExam(baseYear);
                log.info("Grade-specific statistics data collected for year {}", baseYear);
            } catch (Exception e) {
                log.error("Failed to collect grade-specific statistics data: {}", e.getMessage());
            }

            // 3. 주요 자격증 시험 정보 수집 (예시: 정보처리기사)
            // 실제 프로젝트에서는 DB에 저장된 자격증 목록을 순회하며 호출해야 합니다.
            String jmcd = "86500000002"; // 정보처리기사
            try {
                examService.importExamInfo(jmcd, baseYear);
                log.info("Exam data collected for jmcd: {} in year {}", jmcd, baseYear);
            } catch (Exception e) {
                log.error("Failed to collect exam data for jmcd {}: {}", jmcd, e.getMessage());
            }

            log.info("Scheduled data collection completed successfully.");
        } catch (Exception e) {
            log.error("Scheduled data collection failed: {}", e.getMessage());
        }
    }
}
