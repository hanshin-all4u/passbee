package com.passbee.qnet.service;

import com.passbee.config.QnetProperties;
import com.passbee.qnet.client.QnetClient;
import com.passbee.qnet.dto.*;
// 중복 import 제거: ↓↓↓
// import com.passbee.qnet.dto.*;  // (두 번 있던 거 하나 삭제)
import com.passbee.qnet.dto.common.QnetXmlBase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class QnetDataService {

    private final QnetClient qnetClient;
    private final QnetProperties qnetProperties;

    private <T> List<T> fetchData(String endpointKey, Map<String, String> params, Class<T> itemClass) {
        String endpoint = qnetProperties.getEndpoints().get(endpointKey);
        QnetXmlBase<T> response = qnetClient.get(endpoint, params, itemClass);
        if (response == null || response.getBody() == null
                || response.getBody().getItems() == null
                || response.getBody().getItems().getItem() == null) {
            return Collections.emptyList();
        }
        return response.getBody().getItems().getItem();
    }

    // --- 아래 공개 메서드들은 네 코드 그대로 두면 됩니다 ---
    public List<TotalExamStatItem> getTotalExamStats(String baseYY) {
        return fetchData("total-exam", Map.of("baseYY", baseYY), TotalExamStatItem.class);
    }
    public List<GradePassStatItem> getGradeWrittenExamStats(String baseYY) {
        return fetchData("grad-pi-exam", Map.of("baseYY", baseYY), GradePassStatItem.class);
    }
    public List<GradePassStatItem> getGradeWrittenPassRate(String baseYY) {
        return fetchData("grad-pi-pass", Map.of("baseYY", baseYY), GradePassStatItem.class);
    }
    public List<GradePassStatItem> getGradePracticalExamStats(String baseYY) {
        return fetchData("grad-si-exam", Map.of("baseYY", baseYY), GradePassStatItem.class);
    }
    public List<GradePassStatItem> getGradePracticalPassRate(String baseYY) {
        return fetchData("grad-si-pass", Map.of("baseYY", baseYY), GradePassStatItem.class);
    }
    public List<EventYearStatItem> getEventYearWrittenStats(String baseYY, String jmCd) {
        return fetchData("event-year-pi", Map.of("baseYY", baseYY, "jmCd", jmCd), EventYearStatItem.class);
    }
    public List<EventYearStatItem> getEventYearPracticalStats(String baseYY, String jmCd) {
        return fetchData("event-year-si", Map.of("baseYY", baseYY, "jmCd", jmCd), EventYearStatItem.class);
    }
    public List<EventCertGenderStatItem> getEventCertGenderStats(String baseYY, String jmCd) {
        return fetchData("event-cert-year", Map.of("baseYY", baseYY, "jmCd", jmCd), EventCertGenderStatItem.class);
    }
    public List<GradePassStatItem> getCertYearGradeStats(String baseYY) {
        return fetchData("cert-year-grade", Map.of("baseYY", baseYY), GradePassStatItem.class);
    }
    public List<CertAgeStatItem> getCertYearAgeStats(String baseYY) {
        return fetchData("cert-year-age", Map.of("baseYY", baseYY), CertAgeStatItem.class);
    }
    public List<QualificationItem> getQualifications(int pageNo, int numOfRows) {
        return fetchData("qualifications",
                Map.of("pageNo", String.valueOf(pageNo), "numOfRows", String.valueOf(numOfRows)),
                QualificationItem.class);
    }
    public List<QualitativeInfoItem> getQualitativeInfo(String seriesCd) {
        return fetchData("qualitative-info", Map.of("seriesCd", seriesCd), QualitativeInfoItem.class);
    }
    public List<ExamSubjectItem> getExamSubjects(String jmCd) {
        return fetchData("exam-subjects", Map.of("jmCd", jmCd), ExamSubjectItem.class);
    }
    public List<PracticalExamItem> getPracticalItems(String jmCd, String implYY, String implSeq) {
        return fetchData("practical-items",
                Map.of("jmCd", jmCd, "implYY", implYY, "implSeq", implSeq),
                PracticalExamItem.class);
    }
    public List<AgencyItem> getAgencies(int pageNo, int numOfRows) {
        return fetchData("agencies",
                Map.of("pageNo", String.valueOf(pageNo), "numOfRows", String.valueOf(numOfRows)),
                AgencyItem.class);
    }
    public List<RegionalReceptionItem> getRegionalReceptionStats(String baseYY, String grdCd) {
        return fetchData("regional-reception", Map.of("baseYY", baseYY, "grdCd", grdCd), RegionalReceptionItem.class);
    }
    public List<ScoreDistributionItem> getScoreDistribution(String baseYY, String grdCd) {
        return fetchData("score-distribution", Map.of("baseYY", baseYY, "grdCd", grdCd), ScoreDistributionItem.class);
    }
}