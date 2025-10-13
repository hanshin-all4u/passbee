package com.passbee.exam.service;

import com.passbee.exam.ExamSubject;
import com.passbee.exam.ExamSubjectRepository;
import com.passbee.license.License;
import com.passbee.license.LicenseRepository;
import com.passbee.qnet.client.QnetClient;
import com.passbee.qnet.dto.common.QnetXmlBase;
import com.passbee.qnet.dto.ExamSubjectItem;
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
    private final ExamSubjectRepository examSubjectRepository;
    private final LicenseRepository licenseRepository;

    @Value("${external.qnet.endpoints.exam-subjects}")
    private String examSubjectsEndpoint;

    @Transactional
    public void importExamSubjects(String jmcd) {
        Optional<License> licenseOpt = licenseRepository.findById(jmcd);
        if (licenseOpt.isEmpty()) {
            log.warn("License not found for jmcd {}", jmcd);
            return;
        }
        License license = licenseOpt.get();

        // 과목 정보는 루트 패키지의 ExamSubjectItem(jmNm, kmNm, kmYn)을 사용합니다.
        QnetXmlBase<ExamSubjectItem> resp = qnetClient.get(
                examSubjectsEndpoint,
                Map.of("jmCd", jmcd),
                ExamSubjectItem.class
        );
        if (resp == null || resp.getBody() == null || resp.getBody().getItems() == null || resp.getBody().getItems().getItem() == null) {
            log.warn("No exam subjects found for jmcd: {}", jmcd);
            return;
        }
        List<ExamSubjectItem> items = resp.getBody().getItems().getItem();
        for (ExamSubjectItem s : items) {
            ExamSubject es = ExamSubject.builder()
                    .license(license)
                    .jmNm(s.getJmNm())
                    .kmNm(s.getKmNm())
                    .kmYn(s.getKmYn())
                    .build();
            examSubjectRepository.save(es);
        }
    }
}