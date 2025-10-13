package com.passbee.scheduler;

import com.passbee.agency.Agency;
import com.passbee.agency.AgencyRepository;
import com.passbee.exam.PracticalItem;
import com.passbee.exam.PracticalItemRepository;
import com.passbee.exam.service.ExamService;
import com.passbee.license.License;
import com.passbee.license.LicenseRepository;
import com.passbee.qnet.dto.*;
import com.passbee.qnet.entity.Qualification;
import com.passbee.qnet.repository.QualificationRepository;
import com.passbee.qnet.service.QnetDataService;
import com.passbee.statistics.GradeStat;
import com.passbee.statistics.GradeStatRepository;
import com.passbee.statistics.RegionalReception;
import com.passbee.statistics.RegionalReceptionRepository;
import com.passbee.statistics.ScoreDistribution;
import com.passbee.statistics.ScoreDistributionRepository;
import com.passbee.statistics.TotalExamStat;
import com.passbee.statistics.TotalExamStatRepository;
import com.passbee.statistics.StatsService;
import com.passbee.exam.ExamSubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledDataCollector {

    private final QnetDataService qnetDataService;
    private final ExamService examService;
    private final LicenseRepository licenseRepository;
    private final QualificationRepository qualificationRepository;
    private final AgencyRepository agencyRepository;
    private final PracticalItemRepository practicalItemRepository;
    private final TotalExamStatRepository totalExamStatRepository;
    private final StatsService statsService;
    private final GradeStatRepository gradeStatRepository;
    private final RegionalReceptionRepository regionalReceptionRepository;
    private final ScoreDistributionRepository scoreDistributionRepository;
    private final DataCollectionProgressService progressService;
    private final ExamSubjectRepository examSubjectRepository;

    // 수집 간 서버 과부하 방지를 위한 짧은 지연 (기존 500ms → 50ms)
    private static final long THROTTLE_MS = 50L;

    // 초기 데이터 수집 완료 여부 플래그
    private final AtomicBoolean initialCollectionDone = new AtomicBoolean(false);
    
    // 수집 작업 타입 상수
    private static final String TYPE_QUALIFICATIONS = "QUALIFICATIONS";
    private static final String TYPE_QUALIFICATIONS_TO_QUALIFICATION = "QUALIFICATIONS_TO_QUALIFICATION";
    private static final String TYPE_LICENSE_DETAILS = "LICENSE_DETAILS";
    private static final String TYPE_EXAM_SUBJECTS = "EXAM_SUBJECTS";
    private static final String TYPE_PRACTICAL_ITEMS = "PRACTICAL_ITEMS";
    private static final String TYPE_AGENCIES = "AGENCIES";
    private static final String TYPE_STATISTICS = "STATISTICS";
    private static final String TYPE_SCORE_DISTRIBUTIONS = "SCORE_DISTRIBUTIONS";
    
    /**
     * 애플리케이션 시작 시 자동으로 초기 데이터 수집
     * 별도 스레드에서 실행하여 애플리케이션 시작을 차단하지 않음
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("========================================");
        log.info("애플리케이션 시작 완료 - 초기 데이터 수집을 시작합니다...");
        log.info("========================================");
        
        // 별도 스레드에서 실행하여 애플리케이션 시작을 차단하지 않음
        new Thread(() -> {
            try {
                collectAllData();
                initialCollectionDone.set(true);
                log.info("========================================");
                log.info("초기 데이터 수집이 완료되었습니다.");
                log.info("이제 매일 자정에 자동으로 데이터가 업데이트됩니다.");
                log.info("========================================");
            } catch (Exception e) {
                log.error("초기 데이터 수집 중 오류 발생", e);
            }
        }, "InitialDataCollector").start();
    }
    
    /**
     * 매일 자정(00:00)에 전체 데이터 자동 수집
     * cron: 초 분 시 일 월 요일
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledCollectAllData() {
        // 초기 수집이 완료되지 않았으면 스케줄 실행하지 않음
        if (!initialCollectionDone.get()) {
            log.info("초기 데이터 수집이 진행 중이므로 스케줄 수집을 건너뜁니다.");
            return;
        }
        
        log.info("========================================");
        log.info("스케줄된 데이터 수집 시작 (매일 자정)");
        log.info("========================================");
        collectAllData();
    }
    
    /**
     * 매주 월요일 오전 3시에 전체 데이터 재수집 (보험용)
     * cron: 초 분 시 일 월 요일 (0 = 일요일, 1 = 월요일)
     */
    @Scheduled(cron = "0 0 3 * * 1")
    public void weeklyFullRefresh() {
        if (!initialCollectionDone.get()) {
            log.info("초기 데이터 수집이 진행 중이므로 주간 수집을 건너뜁니다.");
            return;
        }
        
        log.info("========================================");
        log.info("주간 전체 데이터 재수집 시작");
        log.info("========================================");
        collectAllData();
    }

    /**
     * 전체 데이터 수집 메인 메서드
     */
    public void collectAllData() {
        log.info("Starting scheduled data collection...");
        try {
            collectQualifications();
            collectQualificationsToQualificationTable();
            enrichLicenseDetails();
            collectExamSubjects();
            collectPracticalItems();
            collectAgencies();
            collectStatistics();
            collectScoreDistributions();
            log.info("Scheduled data collection process finished.");
        } catch (Exception e) {
            log.error("Scheduled data collection failed", e);
        }
    }

    @Transactional
    public void collectQualifications() throws InterruptedException {
        log.info("===== 1. Collecting qualifications... =====");
        
        // 진행 상태 확인 및 초기화
        DataCollectionProgress progress = progressService.getOrCreate(TYPE_QUALIFICATIONS);
        int pageNo = 1;
        int totalSavedCount = 0;
        
        // 이전에 중단된 곳부터 재개
        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS && 
            progress.getCurrentPage() != null) {
            pageNo = progress.getCurrentPage();
            totalSavedCount = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0;
            log.info("🔄 Resuming from page {} (already processed {} items)", pageNo, totalSavedCount);
        } else {
            progressService.markAsStarted(TYPE_QUALIFICATIONS);
        }
        
        try {
        while (true) {
            List<QualificationItem> items = qnetDataService.getQualifications(pageNo, 200);
            if (CollectionUtils.isEmpty(items)) {
                log.warn("No qualification data found at page {}. Stopping collection.", pageNo);
                break;
            }

            log.info("Page {} of qualifications found {} items.", pageNo, items.size());
            List<License> toSave = new ArrayList<>();
            for (QualificationItem item : items) {
                License license = licenseRepository.findById(item.getJmcd()).orElseGet(() ->
                        License.builder().jmcd(item.getJmcd()).build()
                );
                // 최신 필드 동기화 (업서트)
                license.setJmfldnm(item.getJmfldnm());
                license.setSeriescd(item.getSeriescd());
                license.setSeriesnm(item.getSeriesnm());
                license.setQualgbcd(item.getQualgbcd());
                license.setQualgbnm(item.getQualgbnm());
                license.setMdobligfldcd(item.getMdobligfldcd());
                license.setMdobligfldnm(item.getMdobligfldnm());
                license.setObligfldcd(item.getObligfldcd());
                license.setObligfldnm(item.getObligfldnm());
                toSave.add(license);
            }
            if (!toSave.isEmpty()) {
                    licenseRepository.saveAll(toSave);
                }
                totalSavedCount += items.size();
                
                // 진행 상태 저장 (체크포인트)
                progressService.updatePageProgress(TYPE_QUALIFICATIONS, pageNo + 1, totalSavedCount);
                
                pageNo++;
                Thread.sleep(THROTTLE_MS);
            }
            
            // 완료 처리
            progressService.markAsCompleted(TYPE_QUALIFICATIONS);
            log.info("===== 1. Finished collecting qualifications. Total processed: {} =====", totalSavedCount);
        } catch (Exception e) {
            progressService.markAsFailed(TYPE_QUALIFICATIONS, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void collectQualificationsToQualificationTable() throws InterruptedException {
        log.info("===== 1-1. Collecting qualifications to Qualification table... =====");
        
        DataCollectionProgress progress = progressService.getOrCreate(TYPE_QUALIFICATIONS_TO_QUALIFICATION);
        int pageNo = 1;
        int totalSavedCount = 0;
        
        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS && 
            progress.getCurrentPage() != null) {
            pageNo = progress.getCurrentPage();
            totalSavedCount = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0;
            log.info("🔄 Resuming from page {} (already processed {} items)", pageNo, totalSavedCount);
        } else {
            progressService.markAsStarted(TYPE_QUALIFICATIONS_TO_QUALIFICATION);
        }
        
        try {
            while (true) {
                List<QualificationItem> items = qnetDataService.getQualifications(pageNo, 200);
                if (CollectionUtils.isEmpty(items)) {
                    log.warn("No qualification data found at page {}. Stopping collection.", pageNo);
                    break;
                }

                log.info("Page {} of qualifications found {} items.", pageNo, items.size());
                List<Qualification> toSave = new ArrayList<>();
                for (QualificationItem item : items) {
                    Qualification qualification = qualificationRepository.findById(item.getJmcd()).orElseGet(() ->
                            new Qualification()
                    );
                    qualification.setJmcd(item.getJmcd());
                    qualification.setJmfldnm(item.getJmfldnm());
                    qualification.setSeriescd(item.getSeriescd());
                    qualification.setSeriesnm(item.getSeriesnm());
                    qualification.setQualgbcd(item.getQualgbcd());
                    qualification.setQualgbnm(item.getQualgbnm());
                    qualification.setMdobligfldcd(item.getMdobligfldcd());
                    qualification.setMdobligfldnm(item.getMdobligfldnm());
                    qualification.setObligfldcd(item.getObligfldcd());
                    qualification.setObligfldnm(item.getObligfldnm());
                    toSave.add(qualification);
                }
                if (!toSave.isEmpty()) {
                    qualificationRepository.saveAll(toSave);
            }
            totalSavedCount += items.size();
                progressService.updatePageProgress(TYPE_QUALIFICATIONS_TO_QUALIFICATION, pageNo + 1, totalSavedCount);
            pageNo++;
            Thread.sleep(THROTTLE_MS);
            }
            progressService.markAsCompleted(TYPE_QUALIFICATIONS_TO_QUALIFICATION);
            log.info("===== 1-1. Finished collecting qualifications to Qualification table. Total processed: {} =====", totalSavedCount);
        } catch (Exception e) {
            progressService.markAsFailed(TYPE_QUALIFICATIONS_TO_QUALIFICATION, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void enrichLicenseDetails() throws InterruptedException {
        log.info("===== 2. Enriching license details... =====");
        List<License> licenses = licenseRepository.findAll();
        if (CollectionUtils.isEmpty(licenses)) {
            log.warn("No licenses found in DB to enrich. Skipping.");
            return;
        }

        DataCollectionProgress progress = progressService.getOrCreate(TYPE_LICENSE_DETAILS);
        int startIndex = 0;
        final int[] enrichedCount = {0};
        
        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS && 
            progress.getCurrentIndex() != null) {
            startIndex = progress.getCurrentIndex();
            enrichedCount[0] = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0;
            log.info("🔄 Resuming from index {} (already processed {} items)", startIndex, enrichedCount[0]);
        } else {
            progressService.markAsStarted(TYPE_LICENSE_DETAILS);
        }
        
        try {
        Map<String, List<QualitativeInfoItem>> seriesCache = new HashMap<>();
            for (int i = startIndex; i < licenses.size(); i++) {
                License license = licenses.get(i);
            if (license.getSummary() == null || license.getTrend() == null) {
                List<QualitativeInfoItem> details = seriesCache.computeIfAbsent(
                        license.getSeriescd(),
                        sc -> qnetDataService.getQualitativeInfo(sc)
                );
                if (!CollectionUtils.isEmpty(details)) {
                    details.stream()
                            .filter(item -> item.getJmNm().equals(license.getJmfldnm()))
                            .findFirst()
                            .ifPresent(detail -> {
                                license.setSummary(detail.getSummary());
                                license.setJob(detail.getJob());
                                license.setCareer(detail.getCareer());
                                    license.setTrend(detail.getTrend());
                                licenseRepository.save(license);
                                log.info("Enriched: {}", license.getJmfldnm());
                                enrichedCount[0]++; // 실제로 처리된 경우만 카운트
                            });
                }
                Thread.sleep(THROTTLE_MS);
            }
                if (i % 10 == 0) {
                    progressService.updateIndexProgress(TYPE_LICENSE_DETAILS, i + 1, licenses.size(), enrichedCount[0]);
                }
            }
            progressService.markAsCompleted(TYPE_LICENSE_DETAILS);
            log.info("===== 2. Finished enriching license details. Total enriched: {} =====", enrichedCount[0]);
        } catch (Exception e) {
            progressService.markAsFailed(TYPE_LICENSE_DETAILS, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void collectExamSubjects() throws InterruptedException {
        log.info("===== 2-1. Collecting exam subjects... =====");
        // Skip guard: if any exam subjects already exist, mark completed and skip
        if (examSubjectRepository.count() > 0) {
            log.info("Exam subjects already present. Skipping subject collection step.");
            progressService.markAsCompleted(TYPE_EXAM_SUBJECTS);
            return;
        }
        List<License> licenses = licenseRepository.findAll();
        if (CollectionUtils.isEmpty(licenses)) {
            log.warn("No licenses found in DB. Skipping exam subject collection.");
            return;
        }

        DataCollectionProgress progress = progressService.getOrCreate(TYPE_EXAM_SUBJECTS);
        int startIndex = 0;
        int totalSubjectCount = 0;

        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS && 
            progress.getCurrentIndex() != null) {
            startIndex = progress.getCurrentIndex();
            totalSubjectCount = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0;
            log.info("🔄 Resuming from index {} (already processed {} licenses)", startIndex, totalSubjectCount);
        } else {
            progressService.markAsStarted(TYPE_EXAM_SUBJECTS);
        }
        
        try {
            for (int i = startIndex; i < licenses.size(); i++) {
                License license = licenses.get(i);
            String jmcd = license.getJmcd();
            
            try {
                examService.importExamSubjects(jmcd);
                totalSubjectCount++;
                Thread.sleep(THROTTLE_MS);
            } catch (Exception e) {
                log.warn("Failed to collect exam subjects for jmcd: {}. Error: {}", jmcd, e.getMessage());
            }
                
                if (i % 10 == 0) {
                    progressService.updateIndexProgress(TYPE_EXAM_SUBJECTS, i + 1, licenses.size(), totalSubjectCount);
                }
            }
            progressService.markAsCompleted(TYPE_EXAM_SUBJECTS);
            log.info("===== 2-1. Finished collecting exam subjects ({}건). =====", totalSubjectCount);
        } catch (Exception e) {
            progressService.markAsFailed(TYPE_EXAM_SUBJECTS, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void collectPracticalItems() throws InterruptedException {
        log.info("===== 2-2. Collecting practical exam items... =====");
        List<License> licenses = licenseRepository.findAll();
        if (CollectionUtils.isEmpty(licenses)) {
            log.warn("No licenses found in DB. Skipping practical items collection.");
            return;
        }

        progressService.markAsStarted(TYPE_PRACTICAL_ITEMS);
        int currentYear = Year.now().getValue();
        int totalItemCount = 0;

        try {
        // 최근 3년간 데이터 수집
        for (int year = currentYear; year >= currentYear - 2; year--) {
            String implYy = String.valueOf(year);
            
            // 회차는 보통 1~4회까지
            for (int seq = 1; seq <= 4; seq++) {
                String implSeq = String.valueOf(seq);
                
                    int checkedCount = 0;
                    for (License license : licenses) {
                        String jmcd = license.getJmcd();
                        try {
                            List<PracticalExamItem> items = qnetDataService.getPracticalItems(jmcd, implYy, implSeq);
                            if (!CollectionUtils.isEmpty(items)) {
                                for (PracticalExamItem item : items) {
                                    PracticalItem practicalItem = PracticalItem.builder()
                                            .license(license)
                                            .implYy(implYy)
                                            .implSeq(implSeq)
                                            .jmNm(item.getJmNm())
                                            .mtrlNm(item.getMtrlNm())
                                            .mtrlExpl(item.getMtrlExpl())
                                            .build();
                                    practicalItemRepository.save(practicalItem);
                                    totalItemCount++;
                                }
                                log.info("Saved {} practical items for jmcd: {}, year: {}, seq: {}",
                                        items.size(), jmcd, implYy, implSeq);
                            }
                        } catch (Exception e) {
                            log.debug("No practical items for jmcd: {}, year: {}, seq: {}. Error: {}",
                                    jmcd, implYy, implSeq, e.getMessage());
                        } finally {
                            // 항상 진행 상태를 갱신: 저장이 0건이어도 현재 위치를 기록
                            progressService.updatePracticalProgress(
                                    TYPE_PRACTICAL_ITEMS, jmcd, implYy, seq, totalItemCount);
                            checkedCount++;
                            if (checkedCount % 200 == 0) {
                                log.info("[HB] practical scan year={}, seq={}, checked={}, savedTotal={}",
                                        implYy, implSeq, checkedCount, totalItemCount);
                            }
                            Thread.sleep(THROTTLE_MS);
                        }
                    }
                }
            }
            progressService.markAsCompleted(TYPE_PRACTICAL_ITEMS);
            log.info("===== 2-2. Finished collecting practical items. Total saved: {} =====", totalItemCount);
        } catch (Exception e) {
            progressService.markAsFailed(TYPE_PRACTICAL_ITEMS, e.getMessage());
            throw e;
        }
    }

    @Transactional
    public void collectAgencies() throws InterruptedException {
        log.info("===== 3. Collecting agencies... =====");
        
        DataCollectionProgress progress = progressService.getOrCreate(TYPE_AGENCIES);
        int pageNo = 1;
        int totalSavedCount = 0;
        
        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS && 
            progress.getCurrentPage() != null) {
            pageNo = progress.getCurrentPage();
            totalSavedCount = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0;
            log.info("🔄 Resuming from page {} (already processed {} items)", pageNo, totalSavedCount);
        } else {
            agencyRepository.deleteAllInBatch();
            progressService.markAsStarted(TYPE_AGENCIES);
        }
        
        try {
        while (true) {
            List<AgencyItem> items = qnetDataService.getAgencies(pageNo, 200);
            if (CollectionUtils.isEmpty(items)) {
                log.warn("No agency data found at page {}. Stopping collection.", pageNo);
                break;
            }

            log.info("Page {} of agencies found {} items.", pageNo, items.size());
            List<Agency> toSave = new ArrayList<>();
            for (AgencyItem item : items) {
                if (!agencyRepository.existsById(item.getRcogInstiCd())) {
                    toSave.add(Agency.builder()
                            .rcogInstiCd(item.getRcogInstiCd())
                            .rcogInstiNm(item.getRcogInstiNm())
                            .crerRcogRate(item.getCrerRcogRate())
                            .validTermStartDt(item.getValidTermStartDt())
                            .validTermEndDt(item.getValidTermEndDt())
                            .build());
                }
            }
            if (!toSave.isEmpty()) {
                agencyRepository.saveAll(toSave);
            }
            totalSavedCount += items.size();
                progressService.updatePageProgress(TYPE_AGENCIES, pageNo + 1, totalSavedCount);
            pageNo++;
            Thread.sleep(THROTTLE_MS);
        }
            progressService.markAsCompleted(TYPE_AGENCIES);
        log.info("===== 3. Finished collecting agencies. Total saved: {} =====", totalSavedCount);
        } catch (Exception e) {
            progressService.markAsFailed(TYPE_AGENCIES, e.getMessage());
            throw e;
        }
    }

    // Remove broad transaction to allow per-year commits inside StatsService
    public void collectStatistics() throws InterruptedException {
        log.info("===== 4. Collecting all statistics... =====");
        progressService.markAsStarted(TYPE_STATISTICS);
        int lastYear = Year.now().getValue() - 1;

        try {
        for (int year = lastYear; year >= lastYear - 5; year--) {
            try {
                log.info("Collecting year-based stats with per-year transaction for {}...", year);
                statsService.importYear(year); // per-year @Transactional
                Thread.sleep(THROTTLE_MS);
            } catch (Exception yearEx) {
                log.warn("Year {} statistics import failed: {}", year, yearEx.getMessage());
                // continue with next year
            }
        }
            progressService.markAsCompleted(TYPE_STATISTICS);
        log.info("===== 4. Finished collecting all statistics. =====");
        } catch (Exception e) {
            progressService.markAsFailed(TYPE_STATISTICS, e.getMessage());
            throw e;
        }
    }

    private void saveTotalExamStat(int year, TotalExamStatItem item) {
        TotalExamStat stat = TotalExamStat.builder().baseYear(year)
                .writtenApplicants1(Long.parseLong(item.getPilrccnt1()))
                .writtenApplicants2(Long.parseLong(item.getPilrccnt2()))
                .writtenApplicants3(Long.parseLong(item.getPilrccnt3()))
                .writtenApplicants4(Long.parseLong(item.getPilrccnt4()))
                .writtenApplicants5(Long.parseLong(item.getPilrccnt5()))
                .writtenApplicants6(Long.parseLong(item.getPilrccnt6()))
                .practicalApplicants1(Long.parseLong(item.getSilrccnt1()))
                .practicalApplicants2(Long.parseLong(item.getSilrccnt2()))
                .practicalApplicants3(Long.parseLong(item.getSilrccnt3()))
                .practicalApplicants4(Long.parseLong(item.getSilrccnt4()))
                .practicalApplicants5(Long.parseLong(item.getSilrccnt5()))
                .practicalApplicants6(Long.parseLong(item.getSilrccnt6()))
                .writtenPassers1(Long.parseLong(item.getPilpscnt1()))
                .writtenPassers2(Long.parseLong(item.getPilpscnt2()))
                .writtenPassers3(Long.parseLong(item.getPilpscnt3()))
                .writtenPassers4(Long.parseLong(item.getPilpscnt4()))
                .writtenPassers5(Long.parseLong(item.getPilpscnt5()))
                .writtenPassers6(Long.parseLong(item.getPilpscnt6()))
                .practicalPassers1(Long.parseLong(item.getSilpacnt1()))
                .practicalPassers2(Long.parseLong(item.getSilpacnt2()))
                .practicalPassers3(Long.parseLong(item.getSilpacnt3()))
                .practicalPassers4(Long.parseLong(item.getSilpacnt4()))
                .practicalPassers5(Long.parseLong(item.getSilpacnt5()))
                .practicalPassers6(Long.parseLong(item.getSilpacnt6()))
                .build();
        totalExamStatRepository.save(stat);
    }

    private void saveGradeStats(int year, String type, List<GradePassStatItem> items) {
        if (items == null) {
            log.warn("GradeStats for type '{}' is null. Skipping.", type);
            return;
        }
        log.info("Found {} items for GradeStats (Type: {}).", items.size(), type);
        items.forEach(item -> {
            GradeStat stat = new GradeStat(null, year, item.getGradename(), type,
                    item.getStatisyy1(), item.getStatisyy2(), item.getStatisyy3(),
                    item.getStatisyy4(), item.getStatisyy5(), item.getStatisyy6());
            gradeStatRepository.save(stat);
        });
    }

    private void saveRegionalReception(int year, String gradeName, RegionalReceptionItem item) {
        RegionalReception reception = RegionalReception.builder()
                .baseYear(year)
                .gradeName(gradeName)
                .residence(item.getAbdAddr())
                .receptionBranch(item.getBrchNm())
                .examName(item.getImplPlanNm())
                .licenseName(item.getJmFldNm())
                .receptionCount(Integer.parseInt(item.getRecptCnt()))
                .round(Integer.parseInt(item.getSeqNo()))
                .build();
        regionalReceptionRepository.save(reception);
    }

    @Transactional
    public void collectScoreDistributions() throws InterruptedException {
        log.info("===== 5. Collecting score distributions... =====");
        
        DataCollectionProgress progress = progressService.getOrCreate(TYPE_SCORE_DISTRIBUTIONS);
        int startYear = Year.now().getValue() - 1;
        int totalSavedCount = 0;
        
        // 진행 상태 확인 및 초기화
        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS && 
            progress.getCurrentIndex() != null) {
            startYear = progress.getCurrentIndex();
            totalSavedCount = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0;
            log.info("🔄 Resuming score distributions from year {} (already processed {} items)", startYear, totalSavedCount);
        } else {
            progressService.markAsStarted(TYPE_SCORE_DISTRIBUTIONS);
        }

        try {
        // 최근 5년간 점수 분포 수집 (더 많은 데이터)
        for (int year = startYear; year >= startYear - 4; year--) {
            final int currentYear = year;
            String yearStr = String.valueOf(currentYear);
            log.info("Collecting score distributions for year {}...", yearStr);

            // 기존 데이터 삭제 (중복 방지)
            scoreDistributionRepository.deleteByBaseYear(currentYear);

            // 등급별로 점수 분포 수집
            Map<String, String> gradeCodes = Map.of(
                "기술사", "10", 
                "기능장", "20", 
                "기사", "30", 
                "산업기사", "31", 
                "기능사", "40"
            );

            for (Map.Entry<String, String> entry : gradeCodes.entrySet()) {
                final String gradeName = entry.getKey();
                final String gradeCode = entry.getValue();

                try {
                    List<ScoreDistributionItem> items = qnetDataService.getScoreDistribution(yearStr, gradeCode);
                    
                    if (!CollectionUtils.isEmpty(items)) {
                        log.info("Found {} score distribution items for year: {}, grade: {}", 
                                 items.size(), yearStr, gradeName);
                        
                        for (ScoreDistributionItem item : items) {
                            try {
                                ScoreDistribution distribution = ScoreDistribution.builder()
                                        .baseYear(currentYear)
                                        .gradeName(gradeName)
                                        .implSeq(item.getImplSeq())
                                        .examTypeName(item.getComCdNm())
                                        .licenseName(item.getJmFldNm())
                                        .subjectName(item.getKmNm())
                                        .score40(parseIntSafely(item.getKmPnt40()))
                                        .score50(parseIntSafely(item.getKmPnt50()))
                                        .score60(parseIntSafely(item.getKmPnt60()))
                                        .score70(parseIntSafely(item.getKmPnt70()))
                                        .score80(parseIntSafely(item.getKmPnt80()))
                                        .score81(parseIntSafely(item.getKmPnt81()))
                                        .scoreAvg(parseDoubleSafely(item.getKmPntAvg()))
                                        .build();
                                scoreDistributionRepository.save(distribution);
                                totalSavedCount++;
                            } catch (Exception e) {
                                log.warn("Failed to save score distribution: {}. Error: {}", item, e.getMessage());
                            }
                        }
                    }
                    Thread.sleep(THROTTLE_MS);
                } catch (Exception e) {
                    log.warn("Failed to collect score distributions for year: {}, grade: {}. Error: {}", 
                             yearStr, gradeName, e.getMessage());
                }
            }
            
            // 연도별 진행 상태 업데이트
            progressService.updateIndexProgress(TYPE_SCORE_DISTRIBUTIONS, year - 1, startYear - 4, totalSavedCount);
        }
            progressService.markAsCompleted(TYPE_SCORE_DISTRIBUTIONS);
        log.info("===== 5. Finished collecting score distributions. Total saved: {} =====", totalSavedCount);
        } catch (Exception e) {
            progressService.markAsFailed(TYPE_SCORE_DISTRIBUTIONS, e.getMessage());
            throw e;
        }
    }

    private Integer parseIntSafely(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDoubleSafely(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}