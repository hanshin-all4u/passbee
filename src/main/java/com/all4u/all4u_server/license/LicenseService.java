package com.all4u.all4u_server.license;

import com.all4u.all4u_server.qnet.dto.QualitativeInfoItem;
import com.all4u.all4u_server.qnet.entity.Qualification;
import com.all4u.all4u_server.qnet.repository.QualificationRepository;
import com.all4u.all4u_server.qnet.service.QnetDataService;
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
    private final QnetDataService qnetDataService;

    // 이 메소드는 qualification 테이블의 원본 데이터를 license 테이블로 옮기는 역할을 합니다.
    @Transactional
    public void synchronizeLicenses() {
        List<Qualification> qualifications = qualificationRepository.findAll();
        for (Qualification q : qualifications) {
            // findByJmcd를 findById로 변경합니다.
            licenseRepository.findById(q.getJmcd()).orElseGet(() -> {
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
        List<QualitativeInfoItem> details = qnetDataService.getQualitativeInfo(license.getSeriescd());

        details.stream()
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