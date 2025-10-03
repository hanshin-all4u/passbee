package com.passbee.scheduler;

import com.passbee.agency.Agency;
import com.passbee.agency.AgencyRepository;
import com.passbee.license.License;
import com.passbee.license.LicenseRepository;
import com.passbee.qnet.dto.*;
import com.passbee.qnet.dto.*;
import com.passbee.qnet.service.QnetDataService;
import com.passbee.statistics.GradeStat;
import com.passbee.statistics.GradeStatRepository;
import com.passbee.statistics.RegionalReception;
import com.passbee.statistics.RegionalReceptionRepository;
import com.passbee.statistics.TotalExamStat;
import com.passbee.statistics.TotalExamStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger; // AtomicInteger import 추가

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledDataCollector {

    private final QnetDataService qnetDataService;
    private final LicenseRepository licenseRepository;
    private final AgencyRepository agencyRepository;
    private final TotalExamStatRepository totalExamStatRepository;
    private final GradeStatRepository gradeStatRepository;
    private final RegionalReceptionRepository regionalReceptionRepository;

    public void collectAllData() {
        log.info("Starting scheduled data collection...");
        try {
            collectQualifications();
            enrichLicenseDetails();
            collectAgencies();
            collectStatistics();
            log.info("Scheduled data collection process finished.");
        } catch (Exception e) {
            log.error("Scheduled data collection failed", e);
        }
    }

    @Transactional
    public void collectQualifications() throws InterruptedException {
        log.info("===== 1. Collecting qualifications... =====");
        int pageNo = 1;
        int totalSavedCount = 0;
        while (true) {
            List<QualificationItem> items = qnetDataService.getQualifications(pageNo, 100);
            if (CollectionUtils.isEmpty(items)) {
                log.warn("No qualification data found at page {}. Stopping collection.", pageNo);
                break;
            }

            log.info("Page {} of qualifications found {} items.", pageNo, items.size());
            for (QualificationItem item : items) {
                if (!licenseRepository.existsById(item.getJmcd())) {
                    License license = License.builder()
                            .jmcd(item.getJmcd()).jmfldnm(item.getJmfldnm())
                            .seriescd(item.getSeriescd()).seriesnm(item.getSeriesnm())
                            .qualgbcd(item.getQualgbcd()).qualgbnm(item.getQualgbnm())
                            .mdobligfldcd(item.getMdobligfldcd()).mdobligfldnm(item.getMdobligfldnm())
                            .obligfldcd(item.getObligfldcd()).obligfldnm(item.getObligfldnm())
                            .build();
                    licenseRepository.save(license);
                }
            }
            totalSavedCount += items.size();
            pageNo++;
            Thread.sleep(500);
        }
        log.info("===== 1. Finished collecting qualifications. Total processed: {} =====", totalSavedCount);
    }

    @Transactional
    public void enrichLicenseDetails() throws InterruptedException {
        log.info("===== 2. Enriching license details... =====");
        List<License> licenses = licenseRepository.findAll();
        if (CollectionUtils.isEmpty(licenses)) {
            log.warn("No licenses found in DB to enrich. Skipping.");
            return;
        }

        // int 대신 AtomicInteger 사용
        AtomicInteger enrichedCount = new AtomicInteger(0);
        for (License license : licenses) {
            if (license.getSummary() == null) {
                List<QualitativeInfoItem> details = qnetDataService.getQualitativeInfo(license.getSeriescd());
                if (!CollectionUtils.isEmpty(details)) {
                    details.stream()
                            .filter(item -> item.getJmNm().equals(license.getJmfldnm()))
                            .findFirst()
                            .ifPresent(detail -> {
                                license.setSummary(detail.getSummary());
                                license.setJob(detail.getJob());
                                license.setCareer(detail.getCareer());
                                licenseRepository.save(license);
                                // AtomicInteger의 값을 1 증가시킴
                                enrichedCount.incrementAndGet();
                                log.info("Enriched: {}", license.getJmfldnm());
                            });
                }
                Thread.sleep(500);
            }
        }
        log.info("===== 2. Finished enriching license details. Total enriched: {} =====", enrichedCount.get());
    }

    @Transactional
    public void collectAgencies() throws InterruptedException {
        log.info("===== 3. Collecting agencies... =====");
        agencyRepository.deleteAllInBatch();
        int pageNo = 1;
        int totalSavedCount = 0;
        while (true) {
            List<AgencyItem> items = qnetDataService.getAgencies(pageNo, 100);
            if (CollectionUtils.isEmpty(items)) {
                log.warn("No agency data found at page {}. Stopping collection.", pageNo);
                break;
            }

            log.info("Page {} of agencies found {} items.", pageNo, items.size());
            for (AgencyItem item : items) {
                if (!agencyRepository.existsById(item.getRcogInstiCd())) {
                    Agency agency = Agency.builder()
                            .rcogInstiCd(item.getRcogInstiCd())
                            .agencyName(item.getRcogInstiNm())
                            .recognitionRate(item.getCrerRcogRate())
                            .build();
                    agencyRepository.save(agency);
                }
            }
            totalSavedCount += items.size();
            pageNo++;
            Thread.sleep(500);
        }
        log.info("===== 3. Finished collecting agencies. Total saved: {} =====", totalSavedCount);
    }

    @Transactional
    public void collectStatistics() throws InterruptedException {
        log.info("===== 4. Collecting all statistics... =====");
        int lastYear = Year.now().getValue() - 1;

        for (int year = lastYear; year >= lastYear - 5; year--) {
            final int currentYear = year;
            String yearStr = String.valueOf(currentYear);
            log.info("Collecting year-based stats for base year {}...", yearStr);

            totalExamStatRepository.deleteByBaseYear(currentYear);
            gradeStatRepository.deleteByBaseYear(currentYear);
            regionalReceptionRepository.deleteByBaseYear(currentYear);

            List<TotalExamStatItem> totalExamStats = qnetDataService.getTotalExamStats(yearStr);
            log.info("Found {} items for TotalExamStats.", totalExamStats.size());
            totalExamStats.forEach(item -> saveTotalExamStat(currentYear, item));
            Thread.sleep(500);

            saveGradeStats(currentYear, "WRITTEN_APPLICANTS", qnetDataService.getGradeWrittenExamStats(yearStr));
            Thread.sleep(500);
            saveGradeStats(currentYear, "WRITTEN_PASS_RATE", qnetDataService.getGradeWrittenPassRate(yearStr));
            Thread.sleep(500);
            saveGradeStats(currentYear, "PRACTICAL_APPLICANTS", qnetDataService.getGradePracticalExamStats(yearStr));
            Thread.sleep(500);
            saveGradeStats(currentYear, "PRACTICAL_PASS_RATE", qnetDataService.getGradePracticalPassRate(yearStr));
            Thread.sleep(500);
            saveGradeStats(currentYear, "CERT_BY_GRADE", qnetDataService.getCertYearGradeStats(yearStr));
            Thread.sleep(500);

            Map<String, String> gradeCodes = Map.of("기술사", "10", "기능장", "20", "기사", "30", "산업기사", "31", "기능사", "40");
            for (Map.Entry<String, String> entry : gradeCodes.entrySet()) {
                final String gradeName = entry.getKey();
                List<RegionalReceptionItem> regionalStats = qnetDataService.getRegionalReceptionStats(yearStr, entry.getValue());
                log.info("Found {} items for RegionalReceptionStats (Grade: {}).", regionalStats.size(), gradeName);
                regionalStats.forEach(item -> saveRegionalReception(currentYear, gradeName, item));
                Thread.sleep(500);
            }
        }
        log.info("===== 4. Finished collecting all statistics. =====");
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
}