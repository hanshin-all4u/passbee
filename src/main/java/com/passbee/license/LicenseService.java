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

    // --- 기존에 있던 데이터 수집 관련 메소드들 (그대로 유지) ---

    // 이 메소드는 qualification 테이블의 원본 데이터를 license 테이블로 옮기는 역할을 합니다.
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

    /**
     * API 요청을 위해 모든 자격증 목록을 조회하는 메소드
     * @return DB에 저장된 모든 License 엔티티 목록
     */
    @Transactional(readOnly = true) // 데이터 변경이 없는 조회 전용 트랜잭션
    public List<License> findAllLicenses() {
        return licenseRepository.findAll();
    }

    /**
     * ID(jmcd)를 이용해 특정 자격증 하나의 상세 정보를 조회하는 메소드
     * @param jmcd 조회할 자격증의 고유 종목코드
     * @return 찾아낸 License 엔티티
     * @throws IllegalArgumentException 해당 ID의 자격증이 없을 경우 예외 발생
     */
    @Transactional(readOnly = true)
    public License findLicenseById(String jmcd) {
        // LicenseRepository를 사용해 DB에서 jmcd로 자격증을 찾습니다.
        // 만약 없으면, "해당 자격증을 찾을 수 없습니다" 라는 에러 메시지를 보냅니다.
        return licenseRepository.findById(jmcd)
                .orElseThrow(() -> new IllegalArgumentException("해당 자격증을 찾을 수 없습니다. id=" + jmcd));
    }
}