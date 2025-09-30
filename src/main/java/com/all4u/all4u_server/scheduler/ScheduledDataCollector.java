package com.all4u.all4u_server.scheduler;

import com.all4u.all4u_server.agency.Agency;
import com.all4u.all4u_server.agency.AgencyRepository;
import com.all4u.all4u_server.license.License;
import com.all4u.all4u_server.license.LicenseRepository;
import com.all4u.all4u_server.qnet.dto.*;
import com.all4u.all4u_server.qnet.service.QnetDataService;
import com.all4u.all4u_server.statistics.GradeStat;
import com.all4u.all4u_server.statistics.GradeStatRepository;
import com.all4u.all4u_server.statistics.RegionalReception;
import com.all4u.all4u_server.statistics.RegionalReceptionRepository;
import com.all4u.all4u_server.statistics.TotalExamStat;
import com.all4u.all4u_server.statistics.TotalExamStatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledDataCollector {

    // StatService, StatGradeService 의존성 제거
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
            log.info("Scheduled data collection completed successfully.");
        } catch (Exception e) {
            log.error("Scheduled data collection failed", e);
        }
    }

    @Transactional
    public void collectQualifications() throws InterruptedException {
        log.info("Collecting qualifications...");
        int pageNo = 1;
        while (true) {
            List<QualificationItem> items = qnetDataService.getQualifications(pageNo, 100);
            if (items == null || items.isEmpty()) break;

            for (QualificationItem item : items) {
                licenseRepository.findByJmcd(item.getJmcd()).orElseGet(() -> {
                    License license = License.builder()
                            .jmcd(item.getJmcd()).jmfldnm(item.getJmfldnm())
                            .seriescd(item.getSeriescd()).seriesnm(item.getSeriesnm())
                            .qualgbcd(item.getQualgbcd()).qualgbnm(item.getQualgbnm())
                            .mdobligfldcd(item.getMdobligfldcd()).mdobligfldnm(item.getMdobligfldnm())
                            .obligfldcd(item.getObligfldcd()).obligfldnm(item.getObligfldnm())
                            .build();
                    return licenseRepository.save(license);
                });
            }
            log.info("Page {} of qualifications collected.", pageNo);
            pageNo++;
            Thread.sleep(500);
        }
    }

    @Transactional
    public void enrichLicenseDetails() throws InterruptedException {
        log.info("Enriching license details...");
        List<License> licenses = licenseRepository.findAll();
        for (License license : licenses) {
            if (license.getSummary() == null) {
                List<QualitativeInfoItem> details = qnetDataService.getQualitativeInfo(license.getSeriescd());
                details.stream()
                        .filter(item -> item.getJmNm().equals(license.getJmfldnm()))
                        .findFirst()
                        .ifPresent(detail -> {
                            license.setSummary(detail.getSummary());
                            license.setJob(detail.getJob());
                            license.setCareer(detail.getCareer());
                            licenseRepository.save(license);
                            log.info("Enriched: {}", license.getJmfldnm());
                        });
                Thread.sleep(500);
            }
        }
    }

    @Transactional
    public void collectAgencies() throws InterruptedException {
        log.info("Collecting agencies...");
        agencyRepository.deleteAllInBatch();
        int pageNo = 1;
        while (true) {
            List<AgencyItem> items = qnetDataService.getAgencies(pageNo, 100);
            if (items == null || items.isEmpty()) break;

            for (AgencyItem item : items) {
                Agency agency = Agency.builder()
                        .agencyName(item.getRcogInstiNm())
                        .recognitionRate(item.getCrerRcogRate())
                        .build();
                agencyRepository.save(agency);
            }
            log.info("Page {} of agencies collected.", pageNo);
            pageNo++;
            Thread.sleep(500);
        }
    }

    @Transactional
    public void collectStatistics() throws InterruptedException {
        log.info("Collecting all statistics...");
        int lastYear = Year.now().getValue() - 1;

        for (int year = lastYear; year >= lastYear - 5; year--) {
            final int currentYear = year;
            String yearStr = String.valueOf(currentYear);
            log.info("Collecting year-based stats for base year {}...", yearStr);

            totalExamStatRepository.deleteByBaseYear(currentYear);
            gradeStatRepository.deleteByBaseYear(currentYear);
            regionalReceptionRepository.deleteByBaseYear(currentYear);

            qnetDataService.getTotalExamStats(yearStr).forEach(item -> saveTotalExamStat(currentYear, item));
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
                qnetDataService.getRegionalReceptionStats(yearStr, entry.getValue()).forEach(item -> saveRegionalReception(currentYear, gradeName, item));
                Thread.sleep(500);
            }
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
        if (items == null) return;
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

