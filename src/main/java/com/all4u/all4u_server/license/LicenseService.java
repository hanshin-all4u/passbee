package com.all4u.all4u_server.license;

import com.all4u.all4u_server.qnet.dto.QualitativeInfoItem;
import com.all4u.all4u_server.qnet.entity.Qualification;
import com.all4u.all4u_server.qnet.repository.QualificationRepository;
import com.all4u.all4u_server.qnet.service.QnetDataService; // QnetClient 대신 QnetDataService를 사용합니다.
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final QualificationRepository qualificationRepository;
    private final QnetDataService qnetDataService; // QnetClient 의존성을 QnetDataService로 변경합니다.

    // 이 메소드는 qualification 테이블의 원본 데이터를 license 테이블로 옮기는 역할을 합니다.
    @Transactional
    public void synchronizeLicenses() {
        List<Qualification> qualifications = qualificationRepository.findAll();
        for (Qualification q : qualifications) {
            licenseRepository.findByJmcd(q.getJmcd()).orElseGet(() -> {
                License license = new License();
                license.setJmcd(q.getJmcd());
                license.setJmfldnm(q.getJmfldnm());
                license.setSeriescd(q.getSeriescd());
                license.setSeriesnm(q.getSeriesnm());
                license.setQualgbcd(q.getQualgbcd());
                license.setQualgbnm(q.getQualgbnm());
                license.setMdobligfldcd(q.getMdobligfldcd());
                license.setMdobligfldnm(q.getMdobligfldnm());
                license.setObligfldcd(q.getObligfldcd());
                license.setObligfldnm(q.getObligfldnm());
                return licenseRepository.save(license);
            });
        }
    }

    // License 상세 정보를 가져와 기존 License 엔티티에 추가하는 메소드
    @Transactional
    public void enrichLicenseDetails(License license) {
        // QnetDataService를 통해 API를 호출합니다.
        List<QualitativeInfoItem> details = qnetDataService.getQualitativeInfo(license.getSeriescd());

        details.stream()
                // API 응답 목록에서 현재 license와 jmNm(자격명)이 같은 것을 찾습니다.
                .filter(item -> item.getJmNm().equals(license.getJmfldnm()))
                .findFirst()
                .ifPresent(detail -> {
                    license.setSummary(detail.getSummary());
                    license.setJob(detail.getJob());
                    license.setCareer(detail.getCareer());
                    licenseRepository.save(license);
                });
    }
}
