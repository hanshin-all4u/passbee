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
import com.passbee.qnet.repository.QualificationRepository; //
import com.passbee.qnet.service.QnetDataService;
import com.passbee.statistics.GradeStat;
import com.passbee.statistics.GradeStatRepository; //
import com.passbee.statistics.RegionalReception;
import com.passbee.statistics.RegionalReceptionRepository;
import com.passbee.statistics.ScoreDistribution;
import com.passbee.statistics.ScoreDistributionRepository;
import com.passbee.statistics.TotalExamStat;
import com.passbee.statistics.TotalExamStatRepository;
import com.passbee.statistics.StatsService; //
import com.passbee.exam.ExamSubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.time.Year; //
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern; // ▼▼▼ [추가] 정규식 Pattern import

@Slf4j //
@Component
@RequiredArgsConstructor
public class ScheduledDataCollector {

    private final QnetDataService qnetDataService;
    private final ExamService examService;
    private final LicenseRepository licenseRepository;
    private final QualificationRepository qualificationRepository; //
    private final AgencyRepository agencyRepository;
    private final PracticalItemRepository practicalItemRepository;
    private final TotalExamStatRepository totalExamStatRepository;
    private final StatsService statsService;
    private final GradeStatRepository gradeStatRepository; //
    private final RegionalReceptionRepository regionalReceptionRepository;
    private final ScoreDistributionRepository scoreDistributionRepository;
    private final DataCollectionProgressService progressService;
    private final ExamSubjectRepository examSubjectRepository;

    private static final long THROTTLE_MS = 50L; //
    private final AtomicBoolean initialCollectionDone = new AtomicBoolean(false); //

    private static final String TYPE_QUALIFICATIONS = "QUALIFICATIONS"; //
    private static final String TYPE_QUALIFICATIONS_TO_QUALIFICATION = "QUALIFICATIONS_TO_QUALIFICATION"; //
    private static final String TYPE_LICENSE_DETAILS = "LICENSE_DETAILS"; //
    private static final String TYPE_EXAM_SUBJECTS = "EXAM_SUBJECTS"; //
    private static final String TYPE_PRACTICAL_ITEMS = "PRACTICAL_ITEMS"; //
    private static final String TYPE_AGENCIES = "AGENCIES"; //
    private static final String TYPE_STATISTICS = "STATISTICS"; //
    private static final String TYPE_SCORE_DISTRIBUTIONS = "SCORE_DISTRIBUTIONS"; //

    // ▼▼▼ [추가] CSS 스타일 블록 제거를 위한 정규식 패턴 ▼▼▼
    private static final Pattern CSS_BLOCK_PATTERN = Pattern.compile(
            "^\\s*(BODY\\s*\\{[^}]*\\}|P\\s*\\{[^}]*\\}|LI\\s*\\{[^}]*\\}|BR\\s*/?>|-|\\*|\\s)*",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
    );

    // ▼▼▼ [추가] 스타일 태그 제거 헬퍼 메서드 ▼▼▼
    private String cleanStyleTags(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text == null ? null : text.trim(); // null이면 null, 비어있으면 빈 문자열
        }
        return CSS_BLOCK_PATTERN.matcher(text).replaceAll("").trim();
    }


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("========================================");
        log.info("애플리케이션 시작 완료 - 초기 데이터 수집을 시작합니다..."); //
        log.info("========================================");

        new Thread(() -> { //
            try {
                collectAllData();
                initialCollectionDone.set(true);
                log.info("========================================");
                log.info("초기 데이터 수집이 완료되었습니다."); //
                log.info("이제 매일 자정에 자동으로 데이터가 업데이트됩니다."); //
                log.info("========================================");
            } catch (Exception e) {
                log.error("초기 데이터 수집 중 오류 발생", e);
            }
        }, "InitialDataCollector").start(); //
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduledCollectAllData() {
        if (!initialCollectionDone.get()) {
            log.info("초기 데이터 수집이 진행 중이므로 스케줄 수집을 건너뜁니다."); //
            return;
        }

        log.info("========================================");
        log.info("스케줄된 데이터 수집 시작 (매일 자정)"); //
        log.info("========================================");
        collectAllData();
    }

    @Scheduled(cron = "0 0 3 * * 1")
    public void weeklyFullRefresh() {
        if (!initialCollectionDone.get()) {
            log.info("초기 데이터 수집이 진행 중이므로 주간 수집을 건너뜁니다.");
            return; //
        }

        log.info("========================================");
        log.info("주간 전체 데이터 재수집 시작"); //
        log.info("========================================");
        collectAllData();
    }

    public void collectAllData() {
        log.info("Starting scheduled data collection...");
        try { //
            collectQualifications();
            collectQualificationsToQualificationTable();
            enrichLicenseDetails(); // 스타일 제거 로직 포함된 버전 호출
            collectExamSubjects();
            collectPracticalItems();
            collectAgencies();
            collectStatistics();
            collectScoreDistributions(); //
            log.info("Scheduled data collection process finished.");
        } catch (Exception e) {
            log.error("Scheduled data collection failed", e);
        } //
    }

    @Transactional
    public void collectQualifications() throws InterruptedException {
        log.info("===== 1. Collecting qualifications... =====");
        DataCollectionProgress progress = progressService.getOrCreate(TYPE_QUALIFICATIONS); //
        int pageNo = 1; //
        int totalSavedCount = 0;

        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS &&
                progress.getCurrentPage() != null) {
            pageNo = progress.getCurrentPage();
            totalSavedCount = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0; //
            log.info("🔄 Resuming from page {} (already processed {} items)", pageNo, totalSavedCount);
        } else { //
            progressService.markAsStarted(TYPE_QUALIFICATIONS);
        } //

        try {
            while (true) {
                List<QualificationItem> items = qnetDataService.getQualifications(pageNo, 200);
                if (CollectionUtils.isEmpty(items)) { //
                    log.warn("No qualification data found at page {}. Stopping collection.", pageNo);
                    break; //
                }

                log.info("Page {} of qualifications found {} items.", pageNo, items.size());
                List<License> toSave = new ArrayList<>(); //
                for (QualificationItem item : items) {
                    License license = licenseRepository.findById(item.getJmcd()).orElseGet(() ->
                            License.builder().jmcd(item.getJmcd()).build()
                    );
                    license.setJmfldnm(item.getJmfldnm()); //
                    license.setSeriescd(item.getSeriescd()); //
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
                } //
                totalSavedCount += items.size();
                progressService.updatePageProgress(TYPE_QUALIFICATIONS, pageNo + 1, totalSavedCount); //
                pageNo++; //
                Thread.sleep(THROTTLE_MS);
            }

            progressService.markAsCompleted(TYPE_QUALIFICATIONS);
            log.info("===== 1. Finished collecting qualifications. Total processed: {} =====", totalSavedCount); //
        } catch (Exception e) { //
            progressService.markAsFailed(TYPE_QUALIFICATIONS, e.getMessage());
            throw e;
        } //
    }

    @Transactional
    public void collectQualificationsToQualificationTable() throws InterruptedException {
        log.info("===== 1-1. Collecting qualifications to Qualification table... =====");
        DataCollectionProgress progress = progressService.getOrCreate(TYPE_QUALIFICATIONS_TO_QUALIFICATION); //
        int pageNo = 1;
        int totalSavedCount = 0;
        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS &&
                progress.getCurrentPage() != null) { //
            pageNo = progress.getCurrentPage();
            totalSavedCount = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0; //
            log.info("🔄 Resuming from page {} (already processed {} items)", pageNo, totalSavedCount);
        } else { //
            progressService.markAsStarted(TYPE_QUALIFICATIONS_TO_QUALIFICATION);
        } //

        try {
            while (true) {
                List<QualificationItem> items = qnetDataService.getQualifications(pageNo, 200);
                if (CollectionUtils.isEmpty(items)) { //
                    log.warn("No qualification data found at page {}. Stopping collection.", pageNo);
                    break; //
                }

                log.info("Page {} of qualifications found {} items.", pageNo, items.size());
                List<Qualification> toSave = new ArrayList<>(); //
                for (QualificationItem item : items) {
                    Qualification qualification = qualificationRepository.findById(item.getJmcd()).orElseGet(() ->
                            new Qualification()
                    );
                    qualification.setJmcd(item.getJmcd()); //
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
                } //
                if (!toSave.isEmpty()) {
                    qualificationRepository.saveAll(toSave);
                } //
                totalSavedCount += items.size();
                progressService.updatePageProgress(TYPE_QUALIFICATIONS_TO_QUALIFICATION, pageNo + 1, totalSavedCount);
                pageNo++; //
                Thread.sleep(THROTTLE_MS);
            }
            progressService.markAsCompleted(TYPE_QUALIFICATIONS_TO_QUALIFICATION);
            log.info("===== 1-1. Finished collecting qualifications to Qualification table. Total processed: {} =====", totalSavedCount); //
        } catch (Exception e) { //
            progressService.markAsFailed(TYPE_QUALIFICATIONS_TO_QUALIFICATION, e.getMessage());
            throw e;
        } //
    }

    // ▼▼▼ [수정] enrichLicenseDetails 메서드 (스타일 태그 제거 로직 + try-catch 구조 수정) ▼▼▼
    @Transactional
    public void enrichLicenseDetails() throws InterruptedException {
        log.info("===== 2. Enriching and cleaning license details... =====");
        List<License> licenses = licenseRepository.findAll();
        if (CollectionUtils.isEmpty(licenses)) {
            log.warn("No licenses found in DB to enrich. Skipping.");
            progressService.markAsCompleted(TYPE_LICENSE_DETAILS);
            return;
        }

        DataCollectionProgress progress = progressService.getOrCreate(TYPE_LICENSE_DETAILS);
        int startIndex = 0;
        final int[] updatedCount = {0};

        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS &&
                progress.getCurrentIndex() != null && progress.getCurrentIndex() > 0) {
            startIndex = progress.getCurrentIndex();
            updatedCount[0] = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0;
            log.info("🔄 Resuming from index {} (already processed/cleaned {} items)", startIndex, updatedCount[0]);
        } else {
            startIndex = 0;
            updatedCount[0] = 0;
            progressService.markAsStarted(TYPE_LICENSE_DETAILS);
            log.info("🚀 Starting enrichment and cleaning from the beginning.");
        }
        progressService.updateIndexProgress(TYPE_LICENSE_DETAILS, startIndex, licenses.size(), updatedCount[0]);

        // ▼▼▼ [수정] try 블록이 for 루프를 감싸도록 변경 ▼▼▼
        try {
            Map<String, List<QualitativeInfoItem>> seriesCache = new HashMap<>();
            for (int i = startIndex; i < licenses.size(); i++) {
                License license = licenses.get(i);
                boolean needsApiCall = license.getSummary() == null || license.getJob() == null || license.getCareer() == null || license.getTrend() == null;
                boolean potentiallyDirty = license.getSummary() != null && CSS_BLOCK_PATTERN.matcher(license.getSummary()).find()
                        || license.getJob() != null && CSS_BLOCK_PATTERN.matcher(license.getJob()).find()
                        || license.getCareer() != null && CSS_BLOCK_PATTERN.matcher(license.getCareer()).find()
                        || license.getTrend() != null && CSS_BLOCK_PATTERN.matcher(license.getTrend()).find();

                if (needsApiCall || potentiallyDirty) {
                    List<QualitativeInfoItem> details = null;
                    // API 호출 예외는 바깥 catch에서 처리
                    details = seriesCache.computeIfAbsent(
                            license.getSeriescd(),
                            sc -> qnetDataService.getQualitativeInfo(sc) // 여기서 발생하는 예외는 바깥 catch에서 잡힘
                    );
                    Thread.sleep(THROTTLE_MS); // InterruptedException 발생 가능 지점

                    if (!CollectionUtils.isEmpty(details)) {
                        details.stream()
                                .filter(item -> item.getJmNm() != null && item.getJmNm().equals(license.getJmfldnm()))
                                .findFirst()
                                .ifPresent(detail -> {
                                    boolean updated = false;
                                    String cleanSummary = cleanStyleTags(detail.getSummary());
                                    if (license.getSummary() == null || !license.getSummary().equals(cleanSummary)) {
                                        license.setSummary(cleanSummary);
                                        updated = true;
                                    }
                                    String cleanJob = cleanStyleTags(detail.getJob());
                                    if (license.getJob() == null || !license.getJob().equals(cleanJob)) {
                                        license.setJob(cleanJob);
                                        updated = true;
                                    }
                                    String cleanCareer = cleanStyleTags(detail.getCareer());
                                    if (license.getCareer() == null || !license.getCareer().equals(cleanCareer)) {
                                        license.setCareer(cleanCareer);
                                        updated = true;
                                    }
                                    String cleanTrend = cleanStyleTags(detail.getTrend());
                                    if (license.getTrend() == null || !license.getTrend().equals(cleanTrend)) {
                                        license.setTrend(cleanTrend);
                                        updated = true;
                                    }
                                    if (updated) {
                                        licenseRepository.save(license); // save는 @Transactional에 의해 관리됨
                                        log.info("Updated/Cleaned details for: {}", license.getJmfldnm());
                                        updatedCount[0]++;
                                    }
                                });
                    }
                } // end if (needsApiCall or potentiallyDirty)

                if ((i + 1) % 10 == 0 || i == licenses.size() - 1) {
                    progressService.updateIndexProgress(TYPE_LICENSE_DETAILS, i + 1, licenses.size(), updatedCount[0]);
                    log.debug("Progress updated: index {}, processed {}", i + 1, updatedCount[0]);
                }
            } // end for loop

            // ▼▼▼ [수정] 성공 로그는 try 블록 끝, catch 블록 전에 위치 ▼▼▼
            progressService.markAsCompleted(TYPE_LICENSE_DETAILS);
            log.info("===== 2. Finished enriching and cleaning license details. Total updated/cleaned: {} =====", updatedCount[0]);

            // ▼▼▼ [수정] catch 블록들이 try 블록 바로 다음에 오도록 위치 변경 ▼▼▼
        } catch (InterruptedException ie) { // InterruptedException 처리
            log.warn("License detail enrichment interrupted.");
            progressService.markAsFailed(TYPE_LICENSE_DETAILS, "Interrupted: " + ie.getMessage());
            Thread.currentThread().interrupt(); // 인터럽트 상태 복원 중요
        } catch (Exception e) { // 그 외 모든 예외 처리
            // 루프 중 발생한 예외 인덱스 로깅 강화 (i 변수는 catch 블록에서 접근 불가하므로 startIndex 사용)
            int errorIndex = progress.getCurrentIndex() != null ? progress.getCurrentIndex() -1 : startIndex; // 마지막으로 성공한 인덱스 추정
            log.error("Error during license detail enrichment around index {}: {}", errorIndex, e.getMessage(), e);
            progressService.markAsFailed(TYPE_LICENSE_DETAILS, "Failed around index " + errorIndex + ": " + e.getMessage());
        }
        // ▼▼▼ [수정] try-catch 블록이 여기서 끝나도록 함 ▼▼▼
    } // end enrichLicenseDetails method
// ... (Part 1/2 에서 enrichLicenseDetails 메서드까지 작성됨) ...

    @Transactional
    public void collectExamSubjects() throws InterruptedException {
        log.info("===== 2-1. Collecting exam subjects... =====");
        // Skip guard: if any exam subjects already exist, mark completed and skip
        if (examSubjectRepository.count() > 0) { //
            log.info("Exam subjects already present. Skipping subject collection step.");
            progressService.markAsCompleted(TYPE_EXAM_SUBJECTS); //
            return;
        }
        List<License> licenses = licenseRepository.findAll();
        if (CollectionUtils.isEmpty(licenses)) { //
            log.warn("No licenses found in DB. Skipping exam subject collection.");
            return; //
        }

        DataCollectionProgress progress = progressService.getOrCreate(TYPE_EXAM_SUBJECTS);
        int startIndex = 0;
        int totalSubjectCount = 0; //

        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS &&
                progress.getCurrentIndex() != null) {
            startIndex = progress.getCurrentIndex();
            totalSubjectCount = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0; //
            log.info("🔄 Resuming from index {} (already processed {} licenses)", startIndex, totalSubjectCount);
        } else { //
            progressService.markAsStarted(TYPE_EXAM_SUBJECTS);
        } //

        try {
            for (int i = startIndex; i < licenses.size(); i++) {
                License license = licenses.get(i);
                String jmcd = license.getJmcd(); //

                try {
                    examService.importExamSubjects(jmcd);
                    totalSubjectCount++; //
                    Thread.sleep(THROTTLE_MS);
                } catch (Exception e) {
                    log.warn("Failed to collect exam subjects for jmcd: {}. Error: {}", jmcd, e.getMessage());
                } //

                if (i % 10 == 0) {
                    progressService.updateIndexProgress(TYPE_EXAM_SUBJECTS, i + 1, licenses.size(), totalSubjectCount);
                } //
            }
            progressService.markAsCompleted(TYPE_EXAM_SUBJECTS);
            log.info("===== 2-1. Finished collecting exam subjects ({}건). =====", totalSubjectCount); //
        } catch (Exception e) {
            progressService.markAsFailed(TYPE_EXAM_SUBJECTS, e.getMessage());
            throw e; //
        }
    }

    @Transactional
    public void collectPracticalItems() throws InterruptedException {
        log.info("===== 2-2. Collecting practical exam items... =====");
        List<License> licenses = licenseRepository.findAll(); //
        if (CollectionUtils.isEmpty(licenses)) {
            log.warn("No licenses found in DB. Skipping practical items collection.");
            return; //
        }

        progressService.markAsStarted(TYPE_PRACTICAL_ITEMS);
        int currentYear = Year.now().getValue();
        int totalItemCount = 0;
        try { //
            // 최근 3년간 데이터 수집
            for (int year = currentYear; year >= currentYear - 2; year--) {
                String implYy = String.valueOf(year);
                // 회차는 보통 1~4회까지 //
                for (int seq = 1; seq <= 4; seq++) {
                    String implSeq = String.valueOf(seq);
                    int checkedCount = 0; //
                    for (License license : licenses) {
                        String jmcd = license.getJmcd();
                        try { //
                            List<PracticalExamItem> items = qnetDataService.getPracticalItems(jmcd, implYy, implSeq);
                            if (!CollectionUtils.isEmpty(items)) { //
                                for (PracticalExamItem item : items) {
                                    PracticalItem practicalItem = PracticalItem.builder()
                                            .license(license) //
                                            .implYy(implYy)
                                            .implSeq(implSeq) //
                                            .jmNm(item.getJmNm())
                                            .mtrlNm(item.getMtrlNm())
                                            .mtrlExpl(item.getMtrlExpl()) //
                                            .build();
                                    practicalItemRepository.save(practicalItem); //
                                    totalItemCount++;
                                }
                                log.info("Saved {} practical items for jmcd: {}, year: {}, seq: {}",
                                        items.size(), jmcd, implYy, implSeq);
                            } //
                        } catch (Exception e) {
                            log.debug("No practical items for jmcd: {}, year: {}, seq: {}. Error: {}",
                                    jmcd, implYy, implSeq, e.getMessage()); //
                        } finally {
                            // 항상 진행 상태를 갱신: 저장이 0건이어도 현재 위치를 기록
                            progressService.updatePracticalProgress(
                                    TYPE_PRACTICAL_ITEMS, jmcd, implYy, seq, totalItemCount); //
                            checkedCount++;
                            if (checkedCount % 200 == 0) { //
                                log.info("[HB] practical scan year={}, seq={}, checked={}, savedTotal={}",
                                        implYy, implSeq, checkedCount, totalItemCount);
                            } //
                            Thread.sleep(THROTTLE_MS);
                        } //
                    }
                }
            }
            progressService.markAsCompleted(TYPE_PRACTICAL_ITEMS);
            log.info("===== 2-2. Finished collecting practical items. Total saved: {} =====", totalItemCount); //
        } catch (Exception e) { //
            progressService.markAsFailed(TYPE_PRACTICAL_ITEMS, e.getMessage());
            throw e;
        } //
    }

    @Transactional
    public void collectAgencies() throws InterruptedException {
        log.info("===== 3. Collecting agencies... =====");
        DataCollectionProgress progress = progressService.getOrCreate(TYPE_AGENCIES); //
        int pageNo = 1;
        int totalSavedCount = 0;
        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS &&
                progress.getCurrentPage() != null) { //
            pageNo = progress.getCurrentPage();
            totalSavedCount = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0; //
            log.info("🔄 Resuming from page {} (already processed {} items)", pageNo, totalSavedCount);
        } else { //
            agencyRepository.deleteAllInBatch();
            progressService.markAsStarted(TYPE_AGENCIES);
        } //

        try {
            while (true) {
                List<AgencyItem> items = qnetDataService.getAgencies(pageNo, 200);
                if (CollectionUtils.isEmpty(items)) { //
                    log.warn("No agency data found at page {}. Stopping collection.", pageNo);
                    break; //
                }

                log.info("Page {} of agencies found {} items.", pageNo, items.size());
                List<Agency> toSave = new ArrayList<>(); //
                for (AgencyItem item : items) {
                    if (!agencyRepository.existsById(item.getRcogInstiCd())) {
                        toSave.add(Agency.builder()
                                .rcogInstiCd(item.getRcogInstiCd())
                                .rcogInstiNm(item.getRcogInstiNm()) //
                                .crerRcogRate(item.getCrerRcogRate())
                                .validTermStartDt(item.getValidTermStartDt())
                                .validTermEndDt(item.getValidTermEndDt())
                                .build()); //
                    } //
                }
                if (!toSave.isEmpty()) {
                    agencyRepository.saveAll(toSave);
                } //
                totalSavedCount += items.size();
                progressService.updatePageProgress(TYPE_AGENCIES, pageNo + 1, totalSavedCount);
                pageNo++; //
                Thread.sleep(THROTTLE_MS);
            }
            progressService.markAsCompleted(TYPE_AGENCIES);
            log.info("===== 3. Finished collecting agencies. Total saved: {} =====", totalSavedCount); //
        } catch (Exception e) { //
            progressService.markAsFailed(TYPE_AGENCIES, e.getMessage());
            throw e;
        } //
    }

    // Remove broad transaction to allow per-year commits inside StatsService
    public void collectStatistics() throws InterruptedException {
        log.info("===== 4. Collecting all statistics... =====");
        progressService.markAsStarted(TYPE_STATISTICS); //
        int lastYear = Year.now().getValue() - 1;

        try {
            for (int year = lastYear; year >= lastYear - 5; year--) {
                try {
                    log.info("Collecting year-based stats with per-year transaction for {}...", year);
                    statsService.importYear(year); // per-year @Transactional //
                    Thread.sleep(THROTTLE_MS);
                } catch (Exception yearEx) { //
                    log.warn("Year {} statistics import failed: {}", year, yearEx.getMessage());
                } //
            }
            progressService.markAsCompleted(TYPE_STATISTICS);
            log.info("===== 4. Finished collecting all statistics. ====="); //
        } catch (Exception e) {
            progressService.markAsFailed(TYPE_STATISTICS, e.getMessage());
            throw e; //
        }
    }

    private void saveTotalExamStat(int year, TotalExamStatItem item) {
        TotalExamStat stat = TotalExamStat.builder().baseYear(year)
                .writtenApplicants1(Long.parseLong(item.getPilrccnt1()))
                .writtenApplicants2(Long.parseLong(item.getPilrccnt2()))
                .writtenApplicants3(Long.parseLong(item.getPilrccnt3()))
                .writtenApplicants4(Long.parseLong(item.getPilrccnt4()))
                .writtenApplicants5(Long.parseLong(item.getPilrccnt5())) //
                .writtenApplicants6(Long.parseLong(item.getPilrccnt6()))
                .practicalApplicants1(Long.parseLong(item.getSilrccnt1()))
                .practicalApplicants2(Long.parseLong(item.getSilrccnt2()))
                .practicalApplicants3(Long.parseLong(item.getSilrccnt3()))
                .practicalApplicants4(Long.parseLong(item.getSilrccnt4()))
                .practicalApplicants5(Long.parseLong(item.getSilrccnt5())) //
                .practicalApplicants6(Long.parseLong(item.getSilrccnt6()))
                .writtenPassers1(Long.parseLong(item.getPilpscnt1()))
                .writtenPassers2(Long.parseLong(item.getPilpscnt2()))
                .writtenPassers3(Long.parseLong(item.getPilpscnt3()))
                .writtenPassers4(Long.parseLong(item.getPilpscnt4()))
                .writtenPassers5(Long.parseLong(item.getPilpscnt5()))
                .writtenPassers6(Long.parseLong(item.getPilpscnt6())) //
                .practicalPassers1(Long.parseLong(item.getSilpacnt1()))
                .practicalPassers2(Long.parseLong(item.getSilpacnt2()))
                .practicalPassers3(Long.parseLong(item.getSilpacnt3()))
                .practicalPassers4(Long.parseLong(item.getSilpacnt4()))
                .practicalPassers5(Long.parseLong(item.getSilpacnt5()))
                .practicalPassers6(Long.parseLong(item.getSilpacnt6())) //
                .build();
        totalExamStatRepository.save(stat); //
    }

    private void saveGradeStats(int year, String type, List<GradePassStatItem> items) {
        if (items == null) {
            log.warn("GradeStats for type '{}' is null. Skipping.", type);
            return; //
        }
        log.info("Found {} items for GradeStats (Type: {}).", items.size(), type);
        items.forEach(item -> { //
            GradeStat stat = new GradeStat(null, year, item.getGradename(), type,
                    item.getStatisyy1(), item.getStatisyy2(), item.getStatisyy3(),
                    item.getStatisyy4(), item.getStatisyy5(), item.getStatisyy6());
            gradeStatRepository.save(stat);
        });
    } //

    private void saveRegionalReception(int year, String gradeName, RegionalReceptionItem item) {
        RegionalReception reception = RegionalReception.builder()
                .baseYear(year)
                .gradeName(gradeName)
                .residence(item.getAbdAddr())
                .receptionBranch(item.getBrchNm())
                .examName(item.getImplPlanNm()) //
                .licenseName(item.getJmFldNm())
                .receptionCount(Integer.parseInt(item.getRecptCnt()))
                .round(Integer.parseInt(item.getSeqNo()))
                .build();
        regionalReceptionRepository.save(reception); //
    }

    @Transactional
    public void collectScoreDistributions() throws InterruptedException {
        log.info("===== 5. Collecting score distributions... =====");
        DataCollectionProgress progress = progressService.getOrCreate(TYPE_SCORE_DISTRIBUTIONS); //
        int startYear = Year.now().getValue() - 1;
        int totalSavedCount = 0;
        // 진행 상태 확인 및 초기화 //
        if (progress.getStatus() == DataCollectionProgress.CollectionStatus.IN_PROGRESS &&
                progress.getCurrentIndex() != null) {
            startYear = progress.getCurrentIndex();
            totalSavedCount = progress.getProcessedItems() != null ? progress.getProcessedItems() : 0; //
            log.info("🔄 Resuming score distributions from year {} (already processed {} items)", startYear, totalSavedCount);
        } else { //
            progressService.markAsStarted(TYPE_SCORE_DISTRIBUTIONS);
        } //

        try {
            // 최근 5년간 점수 분포 수집 (더 많은 데이터)
            for (int year = startYear; year >= startYear - 4; year--) {
                final int currentYear = year;
                String yearStr = String.valueOf(currentYear); //
                log.info("Collecting score distributions for year {}...", yearStr);
                // 기존 데이터 삭제 (중복 방지) //
                scoreDistributionRepository.deleteByBaseYear(currentYear);
                // 등급별로 점수 분포 수집 //
                Map<String, String> gradeCodes = Map.of(
                        "기술사", "10",
                        "기능장", "20",
                        "기사", "30",
                        "산업기사", "31",
                        "기능사", "40" //
                );
                for (Map.Entry<String, String> entry : gradeCodes.entrySet()) { //
                    final String gradeName = entry.getKey();
                    final String gradeCode = entry.getValue(); //

                    try {
                        List<ScoreDistributionItem> items = qnetDataService.getScoreDistribution(yearStr, gradeCode);
                        if (!CollectionUtils.isEmpty(items)) { //
                            log.info("Found {} score distribution items for year: {}, grade: {}",
                                    items.size(), yearStr, gradeName);
                            for (ScoreDistributionItem item : items) { //
                                try {
                                    ScoreDistribution distribution = ScoreDistribution.builder()
                                            .baseYear(currentYear) //
                                            .gradeName(gradeName)
                                            .implSeq(item.getImplSeq())
                                            .examTypeName(item.getComCdNm()) //
                                            .licenseName(item.getJmFldNm())
                                            .subjectName(item.getKmNm()) //
                                            .score40(parseIntSafely(item.getKmPnt40()))
                                            .score50(parseIntSafely(item.getKmPnt50()))
                                            .score60(parseIntSafely(item.getKmPnt60())) //
                                            .score70(parseIntSafely(item.getKmPnt70()))
                                            .score80(parseIntSafely(item.getKmPnt80())) //
                                            .score81(parseIntSafely(item.getKmPnt81()))
                                            .scoreAvg(parseDoubleSafely(item.getKmPntAvg()))
                                            .build(); //
                                    scoreDistributionRepository.save(distribution); //
                                    totalSavedCount++;
                                } catch (Exception e) {
                                    log.warn("Failed to save score distribution: {}. Error: {}", item, e.getMessage());
                                } //
                            }
                        }
                        Thread.sleep(THROTTLE_MS);
                    } catch (Exception e) { //
                        log.warn("Failed to collect score distributions for year: {}, grade: {}. Error: {}",
                                yearStr, gradeName, e.getMessage());
                    } //
                }

                // 연도별 진행 상태 업데이트
                progressService.updateIndexProgress(TYPE_SCORE_DISTRIBUTIONS, year - 1, startYear - 4, totalSavedCount);
            } //
            progressService.markAsCompleted(TYPE_SCORE_DISTRIBUTIONS);
            log.info("===== 5. Finished collecting score distributions. Total saved: {} =====", totalSavedCount); //
        } catch (Exception e) { //
            progressService.markAsFailed(TYPE_SCORE_DISTRIBUTIONS, e.getMessage());
            throw e;
        } //
    }

    private Integer parseIntSafely(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        } //
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) { //
            return null;
        } //
    }

    private Double parseDoubleSafely(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        } //
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) { //
            return null;
        } //
    }
} // End of ScheduledDataCollector class