package com.passbee.qnet.controller;

import com.passbee.qnet.dto.*;
import com.passbee.qnet.dto.ExamSubjectItem;
import com.passbee.qnet.service.QnetDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Q-Net Open", description = "Q-Net 원본 데이터 프록시 API (프론트 직접 조회용)")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/qnet")
public class QnetOpenController {

    private final QnetDataService service;

    // 목록/개별 데이터 프록시
    @Operation(summary = "자격 종목 목록 조회 (원본)")
    @GetMapping("/qualifications")
    public List<QualificationItem> qualifications(@RequestParam(defaultValue = "1") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        return service.getQualifications(page, size);
    }

    @Operation(summary = "종목 상세(출제경향 등) 조회 (원본)")
    @GetMapping("/qualitative-info")
    public List<QualitativeInfoItem> qualitative(@RequestParam String seriesCd) {
        return service.getQualitativeInfo(seriesCd);
    }

    @Operation(summary = "시험교시/과목 정보 조회 (원본)")
    @GetMapping("/exam-subjects")
    public List<ExamSubjectItem> examSubjects(@RequestParam String jmCd) {
        return service.getExamSubjects(jmCd);
    }

    @Operation(summary = "실기 지참물 조회 (원본)")
    @GetMapping("/practical-items")
    public List<PracticalExamItem> practical(@RequestParam String jmCd,
                                             @RequestParam String implYY,
                                             @RequestParam String implSeq) {
        return service.getPracticalItems(jmCd, implYY, implSeq);
    }

    @Operation(summary = "인정기관 목록 조회 (원본)")
    @GetMapping("/agencies")
    public List<AgencyItem> agencies(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "100") int size) {
        return service.getAgencies(page, size);
    }

    // 통계 프록시
    @Operation(summary = "연도별 응시/합격 통계")
    @GetMapping("/stats/total-exam")
    public List<TotalExamStatItem> total(@RequestParam String baseYY) {
        return service.getTotalExamStats(baseYY);
    }

    @Operation(summary = "등급별 필기 응시/합격률")
    @GetMapping("/stats/grad-pi-exam")
    public List<GradePassStatItem> gradPiExam(@RequestParam String baseYY) {
        return service.getGradeWrittenExamStats(baseYY);
    }

    @Operation(summary = "등급별 필기 합격률")
    @GetMapping("/stats/grad-pi-pass")
    public List<GradePassStatItem> gradPiPass(@RequestParam String baseYY) {
        return service.getGradeWrittenPassRate(baseYY);
    }

    @Operation(summary = "등급별 실기 응시")
    @GetMapping("/stats/grad-si-exam")
    public List<GradePassStatItem> gradSiExam(@RequestParam String baseYY) {
        return service.getGradePracticalExamStats(baseYY);
    }

    @Operation(summary = "등급별 실기 합격률")
    @GetMapping("/stats/grad-si-pass")
    public List<GradePassStatItem> gradSiPass(@RequestParam String baseYY) {
        return service.getGradePracticalPassRate(baseYY);
    }

    @Operation(summary = "종목별 연도별 필기 현황")
    @GetMapping("/stats/event-year-pi")
    public List<EventYearStatItem> eventYearPi(@RequestParam String baseYY, @RequestParam String jmCd) {
        return service.getEventYearWrittenStats(baseYY, jmCd);
    }

    @Operation(summary = "종목별 연도별 실기 현황")
    @GetMapping("/stats/event-year-si")
    public List<EventYearStatItem> eventYearSi(@RequestParam String baseYY, @RequestParam String jmCd) {
        return service.getEventYearPracticalStats(baseYY, jmCd);
    }

    @Operation(summary = "자격취득자 성별/연도별 현황")
    @GetMapping("/stats/event-cert-gender")
    public List<EventCertGenderStatItem> gender(@RequestParam String baseYY, @RequestParam String jmCd) {
        return service.getEventCertGenderStats(baseYY, jmCd);
    }

    @Operation(summary = "자격취득자 연도별 등급별 수")
    @GetMapping("/stats/cert-year-grade")
    public List<GradePassStatItem> certYearGrade(@RequestParam String baseYY) {
        return service.getCertYearGradeStats(baseYY);
    }

    @Operation(summary = "자격취득자 연도별 연령별 수")
    @GetMapping("/stats/cert-year-age")
    public List<CertAgeStatItem> certYearAge(@RequestParam String baseYY) {
        return service.getCertYearAgeStats(baseYY);
    }

    @Operation(summary = "거주지별 접수 현황")
    @GetMapping("/stats/regional-reception")
    public List<RegionalReceptionItem> regional(@RequestParam String baseYY, @RequestParam String grdCd) {
        return service.getRegionalReceptionStats(baseYY, grdCd);
    }

    @Operation(summary = "연도/등급 점수 분포")
    @GetMapping("/stats/score-distribution")
    public List<ScoreDistributionItem> score(@RequestParam String baseYY, @RequestParam String grdCd) {
        return service.getScoreDistribution(baseYY, grdCd);
    }
}


