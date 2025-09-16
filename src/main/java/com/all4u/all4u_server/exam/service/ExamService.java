package com.all4u.all4u_server.exam.service;

import com.all4u.all4u_server.exam.Exam;
import com.all4u.all4u_server.exam.ExamRepository;
import com.all4u.all4u_server.exam.ExamType;
import com.all4u.all4u_server.license.License;
import com.all4u.all4u_server.license.LicenseRepository;
import com.all4u.all4u_server.qnet.client.QnetClient;
import com.all4u.all4u_server.qnet.dto.exam.ExamSubjectItem;
import com.all4u.all4u_server.qnet.dto.common.QnetXmlBase;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.net.URI;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamService {
    private final QnetClient qnetClient;
    private final ExamRepository examRepository;
    private final LicenseRepository licenseRepository;

    @Value("${external.qnet.base-url}")
    private String baseUrl;

    @Value("${external.qnet.endpoints.examSubjects}")
    private String examSubjectsEndpoint;

    @Transactional
    public void importExamInfo(String jmcd, int baseYear) {
        URI uri = URI.create(String.format(
                "%s%s?jmcd=%s&baseYY=%d&%s",
                baseUrl, examSubjectsEndpoint, jmcd, baseYear, qnetClient.keyParam()
        ));

        QnetXmlBase<ExamSubjectItem> resp = qnetClient.get(uri, ExamSubjectItem.class);

        resp.getBody().getItems().getItem().forEach(item -> {
            Optional<License> licenseOpt = licenseRepository.findByJmcd(jmcd);
            if (licenseOpt.isPresent()) {
                License license = licenseOpt.get();

                Exam exam = Exam.builder()
                        .license(license)
                        .round(item.getImplYy() != null ? Integer.parseInt(item.getImplYy()) : null)
                        .examType(ExamType.valueOf(item.getExamPckd().toUpperCase()))
                        .examDate(item.getDocRegStartDt() != null ? item.getDocRegStartDt().toLocalDateTime() : null)
                        .receptionStart(item.getDocRegStartDt() != null ? item.getDocRegStartDt().toLocalDateTime() : null)
                        .receptionEnd(item.getDocRegEndDt() != null ? item.getDocRegEndDt().toLocalDateTime() : null)
                        .writtenFee(item.getFee() != null ? Integer.parseInt(item.getFee()) : null)
                        .practicalFee(item.getFee() != null ? Integer.parseInt(item.getFee()) : null) // 실기 응시료 필드도 함께 채움
                        .writtenExamDate(item.getDocExamStartDt() != null ? item.getDocExamStartDt().toLocalDateTime() : null)
                        .practicalExamDate(item.getDocPassDt() != null ? item.getDocPassDt().toLocalDateTime() : null)
                        .resultDate(item.getDocPassDt() != null ? item.getDocPassDt().toLocalDateTime() : null)
                        .receptionDesk(item.getAcceptCdNm())
                        .notes(item.getEtc())
                        .build();

                examRepository.save(exam);
            }
        });
    }
}
