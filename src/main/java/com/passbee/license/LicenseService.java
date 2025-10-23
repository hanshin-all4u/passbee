package com.passbee.license;

import com.passbee.qnet.dto.QualitativeInfoItem;
import com.passbee.qnet.entity.Qualification;
import com.passbee.qnet.repository.QualificationRepository;
import com.passbee.qnet.service.QnetDataService;
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

    // --- (생략) --- 기존 synchronizeLicenses, enrichLicenseDetails 메소드 ---

    @Transactional
    public void synchronizeLicenses() {
        List<Qualification> qualifications = qualificationRepository.findAll();
        for (Qualification q : qualifications) {
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
                    license.setTrend(detail.getTrend());
                    licenseRepository.save(license);
                });
    }

    @Transactional(readOnly = true)
    public List<License> findAllLicenses() {
        return licenseRepository.findAll();
    }

    /**
     * 키워드로 자격증을 검색하는 메소드 (대소문자 무시)
     * @param keyword 검색할 키워드
     * @return 검색된 License 엔티티 목록
     */
    @Transactional(readOnly = true)
    public List<License> searchLicenses(String keyword) {
        // 새로 추가한 IgnoreCase 메소드를 호출합니다.
        return licenseRepository.findByJmfldnmContainingIgnoreCase(keyword);
    }
}