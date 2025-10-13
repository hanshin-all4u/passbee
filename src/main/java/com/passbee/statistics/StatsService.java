package com.passbee.statistics;

import com.passbee.qnet.dto.*;
import com.passbee.qnet.service.QnetDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final QnetDataService qnetDataService;
    private final TotalExamStatRepository totalExamStatRepository;
    private final GradeStatRepository gradeStatRepository;
    private final RegionalReceptionRepository regionalReceptionRepository;

    @Transactional
    public void importYear(int year) {
        String yearStr = String.valueOf(year);

        // append-only: 기존 데이터는 보존, 신규만 삽입

        List<TotalExamStatItem> total = qnetDataService.getTotalExamStats(yearStr);
        if (!CollectionUtils.isEmpty(total) && !totalExamStatRepository.existsByBaseYear(year)) {
            total.forEach(item -> saveTotalExamStat(year, item));
        }

        saveGradeStats(year, "WRITTEN_APPLICANTS", qnetDataService.getGradeWrittenExamStats(yearStr));
        saveGradeStats(year, "WRITTEN_PASS_RATE", qnetDataService.getGradeWrittenPassRate(yearStr));
        saveGradeStats(year, "PRACTICAL_APPLICANTS", qnetDataService.getGradePracticalExamStats(yearStr));
        saveGradeStats(year, "PRACTICAL_PASS_RATE", qnetDataService.getGradePracticalPassRate(yearStr));
        saveGradeStats(year, "CERT_BY_GRADE", qnetDataService.getCertYearGradeStats(yearStr));

        Map<String, String> gradeCodes = Map.of("기술사", "10", "기능장", "20", "기사", "30", "산업기사", "31", "기능사", "40");
        gradeCodes.forEach((gradeName, grdCd) -> {
            List<RegionalReceptionItem> regional = qnetDataService.getRegionalReceptionStats(yearStr, grdCd);
            if (!CollectionUtils.isEmpty(regional)) {
                regional.forEach(item -> saveRegionalReception(year, gradeName, item));
            }
        });
    }

    @Transactional
    public void importRange(int fromYear, int toYear) {
        int start = Math.min(fromYear, toYear);
        int end = Math.max(fromYear, toYear);
        for (int y = start; y <= end; y++) {
            importYear(y);
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
        if (CollectionUtils.isEmpty(items)) return;
        items.forEach(item -> {
            boolean exists = gradeStatRepository.existsByBaseYearAndGradeNameAndStatType(
                    year, item.getGradename(), type);
            if (!exists) {
                GradeStat stat = new GradeStat(null, year, item.getGradename(), type,
                        item.getStatisyy1(), item.getStatisyy2(), item.getStatisyy3(),
                        item.getStatisyy4(), item.getStatisyy5(), item.getStatisyy6());
                gradeStatRepository.save(stat);
            }
        });
    }

    private void saveRegionalReception(int year, String gradeName, RegionalReceptionItem item) {
        Integer round = Integer.parseInt(item.getSeqNo());
        boolean exists = regionalReceptionRepository
                .existsByBaseYearAndGradeNameAndResidenceAndReceptionBranchAndRound(
                        year, gradeName, item.getAbdAddr(), item.getBrchNm(), round);
        if (!exists) {
            RegionalReception reception = RegionalReception.builder()
                    .baseYear(year)
                    .gradeName(gradeName)
                    .residence(item.getAbdAddr())
                    .receptionBranch(item.getBrchNm())
                    .examName(item.getImplPlanNm())
                    .licenseName(item.getJmFldNm())
                    .receptionCount(Integer.parseInt(item.getRecptCnt()))
                    .round(round)
                    .build();
            regionalReceptionRepository.save(reception);
        }
    }
}


