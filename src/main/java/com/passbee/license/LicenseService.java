package com.passbee.license;

import com.passbee.common.exception.ResourceNotFoundException; // ▼▼▼ [추가]
import com.passbee.exam.ExamSubject; // ▼▼▼ [추가]
import com.passbee.exam.ExamSubjectRepository; // ▼▼▼ [추가]
import com.passbee.exam.PracticalItem; // ▼▼▼ [추가]
import com.passbee.exam.PracticalItemRepository; // ▼▼▼ [추가]
import com.passbee.license.dto.LicenseDetailResponseDto; // ▼▼▼ [추가]
import com.passbee.qnet.dto.QualitativeInfoItem;
import com.passbee.qnet.entity.Qualification;
import com.passbee.qnet.repository.QualificationRepository;
import com.passbee.qnet.service.QnetDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page; // ▼▼▼ [추가]
import org.springframework.data.domain.Pageable; // ▼▼▼ [추가]
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils; // ▼▼▼ [추가]

import java.util.Collections; // ▼▼▼ [추가]
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final QualificationRepository qualificationRepository;
    private final QnetDataService qnetDataService;

    // ▼▼▼ [추가] 상세 조회를 위해 Repository 2개 주입
    private final ExamSubjectRepository examSubjectRepository;
    private final PracticalItemRepository practicalItemRepository;


    // --- (1/2) API 요청을 위한 서비스 메서드들 (수정 및 추가) ---

    /**
     * [신규] 자격증 상세 조회 (REQ-CERT-003)
     * License + ExamSubject + PracticalItem
     */
    @Transactional(readOnly = true)
    public LicenseDetailResponseDto getLicenseDetails(String jmcd) {
        // 1. (필수) License 정보 조회
        License license = licenseRepository.findById(jmcd)
                .orElseThrow(() -> new ResourceNotFoundException("자격증 정보를 찾을 수 없습니다: " + jmcd));

        // 2. (선택) 시험 과목 목록 조회
        List<ExamSubject> subjects = examSubjectRepository.findByLicense_Jmcd(jmcd);
        if (subjects == null) subjects = Collections.emptyList(); // Null 방지

        // 3. (선택) 실기 지참물 목록 조회
        List<PracticalItem> practicalItems = practicalItemRepository.findByLicense_Jmcd(jmcd);
        if (practicalItems == null) practicalItems = Collections.emptyList(); // Null 방지

        // 4. DTO로 조합하여 반환
        return LicenseDetailResponseDto.from(license, subjects, practicalItems);
    }

    /**
     * [수정] 자격증 목록 검색 (페이징, 검색, 분류)
     * (기존 findAllLicenses() 메서드를 대체합니다)
     */
    @Transactional(readOnly = true)
    public Page<License> searchLicenses(String keyword, String seriesnm, Pageable pageable) {

        boolean hasKeyword = StringUtils.hasText(keyword);
        boolean hasSeries = StringUtils.hasText(seriesnm);

        if (hasKeyword && hasSeries) {
            // 1. 키워드 O, 분야 O
            return licenseRepository.findByJmfldnmContainingAndSeriesnm(keyword, seriesnm, pageable);
        } else if (hasKeyword) {
            // 2. 키워드 O, 분야 X (REQ-CERT-002)
            return licenseRepository.findByJmfldnmContaining(keyword, pageable);
        } else if (hasSeries) {
            // 3. 키워드 X, 분야 O (REQ-CERT-001)
            return licenseRepository.findBySeriesnm(seriesnm, pageable);
        } else {
            // 4. 키워드 X, 분야 X (전체 조회)
            return licenseRepository.findAll(pageable);
        }
    }


    // --- (2/2) 데이터 수집 관련 메소드들 (기존 코드 유지) ---

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
                    license.setTrend(detail.getTrend());
                    licenseRepository.save(license);
                });
    }

    // ▼▼▼ [삭제] 오류를 발생시킨 기존 findAllLicenses() 메서드는
    // searchLicenses()로 대체되었으므로 삭제합니다. ▼▼▼
    /*
    @Transactional(readOnly = true)
    public List<License> findAllLicenses() {
        // 이 메서드가 "findByJmfldnmContainingIgnoreCase"를 호출하던
        // 옛날 코드였을 것이므로, searchLicenses로 대체하고 삭제합니다.
        return licenseRepository.findAll();
    }
    */
}