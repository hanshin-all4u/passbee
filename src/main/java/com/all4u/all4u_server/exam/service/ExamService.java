package com.all4u.all4u_server.exam.service;

import com.all4u.all4u_server.exam.Exam;
import com.all4u.all4u_server.exam.ExamRepository;
import com.all4u.all4u_server.license.License;
import com.all4u.all4u_server.license.LicenseRepository;
import com.all4u.all4u_server.qnet.client.QnetClient;
import com.all4u.all4u_server.qnet.dto.common.QnetXmlBase;
import com.all4u.all4u_server.qnet.dto.exam.ExamSubjectItem;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExamService {
    private final QnetClient qnetClient;
    private final ExamRepository examRepository;
    private final LicenseRepository licenseRepository;

    @Value("${external.qnet.endpoints.exam-subjects}")
    private String examSubjectsEndpoint;

    @Transactional
    public void importExamInfo(String jmcd, int baseYear) {
        // QnetClient의 새로운 get 메소드 형식에 맞게 파라미터를 Map으로 전달합니다.
        Map<String, String> params = Map.of("jmCd", jmcd, "baseYY", String.valueOf(baseYear));
        QnetXmlBase<ExamSubjectItem> resp = qnetClient.get(examSubjectsEndpoint, params, ExamSubjectItem.class);

        if (resp == null || resp.getBody() == null || resp.getBody().getItems() == null || resp.getBody().getItems().getItem() == null) {
            log.warn("No exam data found for jmcd: {} and year: {}", jmcd, baseYear);
            return;
        }

        List<ExamSubjectItem> items = resp.getBody().getItems().getItem();
        for (ExamSubjectItem item : items) {
            // 이 부분을 findByJmcd에서 findById로 변경합니다.
            Optional<License> licenseOpt = licenseRepository.findById(jmcd);
            if (licenseOpt.isPresent()) {
                Exam exam = new Exam();
                exam.setLicense(licenseOpt.get());
                exam.setImplYy(item.getImplYy());
                exam.setExamPckd(item.getExamPckd());
                exam.setDocRegStartDt(item.getDocRegStartDt() != null ? item.getDocRegStartDt().toLocalDate().atStartOfDay() : null);
                exam.setDocRegEndDt(item.getDocRegEndDt() != null ? item.getDocRegEndDt().toLocalDate().atStartOfDay() : null);
                exam.setDocExamStartDt(item.getDocExamStartDt() != null ? item.getDocExamStartDt().toLocalDate().atStartOfDay() : null);
                exam.setDocPassDt(item.getDocPassDt() != null ? item.getDocPassDt().toLocalDate().atStartOfDay() : null);
                exam.setFee(item.getFee() != null ? Integer.parseInt(item.getFee()) : null);
                exam.setAcceptCdNm(item.getAcceptCdNm());
                exam.setEtc(item.getEtc());

                examRepository.save(exam);
            }
        }
    }
}