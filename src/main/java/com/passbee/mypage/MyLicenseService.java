package com.passbee.mypage;

import com.passbee.mypage.dto.MyLicenseDtos;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MyLicenseService {

    private final UserLicenseRepository repo;

    // TODO: 실제 LicenseService 주입해서 jmcd → 이름 변환
    private String resolveLicenseName(String jmcd) { return jmcd; }

    @Transactional(readOnly = true)
    public List<MyLicenseDtos.Item> listMine(Long userId) {
        return repo.findByUserIdOrderByObtainedDateDesc(userId).stream()
                .map(e -> new MyLicenseDtos.Item(
                        e.getId(), e.getJmcd(), resolveLicenseName(e.getJmcd()),
                        e.getObtainedDate(), e.getLevelGrade(), e.getScore(),
                        e.getCertificateNo(), e.getIssuer(), e.getExpiresAt(), e.getMemo()
                )).toList();
    }

    @Transactional
    public Long add(Long userId, MyLicenseDtos.Create req) {
        if (repo.existsByUserIdAndJmcdAndObtainedDate(userId, req.jmcd(), req.obtainedDate())) {
            // 동일 자격/날짜 중복 방지
            return repo.findByUserIdOrderByObtainedDateDesc(userId).stream()
                    .filter(x -> x.getJmcd().equals(req.jmcd()) && x.getObtainedDate().equals(req.obtainedDate()))
                    .findFirst().map(UserLicense::getId).orElse(null);
        }
        var saved = repo.save(UserLicense.builder()
                .userId(userId)
                .jmcd(req.jmcd())
                .obtainedDate(req.obtainedDate())
                .levelGrade(req.levelGrade())
                .score(req.score())
                .certificateNo(req.certificateNo())
                .issuer(req.issuer() == null ? "Q-NET" : req.issuer())
                .expiresAt(req.expiresAt())
                .memo(req.memo())
                .build());
        return saved.getId();
    }

    @Transactional
    public void update(Long userId, Long id, MyLicenseDtos.Update req) {
        var e = repo.findByIdAndUserId(id, userId).orElseThrow(() -> new RuntimeException("NOT_FOUND"));
        if (req.levelGrade() != null) e.setLevelGrade(req.levelGrade());
        if (req.score() != null) e.setScore(req.score());
        if (req.certificateNo() != null) e.setCertificateNo(req.certificateNo());
        if (req.issuer() != null) e.setIssuer(req.issuer());
        if (req.expiresAt() != null) e.setExpiresAt(req.expiresAt());
        if (req.memo() != null) e.setMemo(req.memo());
    }

    @Transactional
    public void delete(Long userId, Long id) {
        var e = repo.findByIdAndUserId(id, userId).orElseThrow(() -> new RuntimeException("NOT_FOUND"));
        repo.delete(e);
    }
}