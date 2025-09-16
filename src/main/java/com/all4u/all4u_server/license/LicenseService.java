package com.all4u.all4u_server.license;

import com.all4u.all4u_server.qnet.entity.Qualification;
import com.all4u.all4u_server.qnet.repository.QualificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LicenseService {
    private final LicenseRepository licenseRepository;
    private final QualificationRepository qualificationRepository;

    @Transactional
    public void synchronizeLicenses() {
        List<Qualification> qualifications = qualificationRepository.findAll();
        List<License> licenses = qualifications.stream()
                .map(q -> {
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
                    return license;
                })
                .collect(Collectors.toList());
        licenseRepository.saveAll(licenses);
    }
}
