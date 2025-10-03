package com.passbee.qnet.service;

import com.passbee.qnet.dto.QualificationItem;
import com.passbee.qnet.entity.Qualification;
import com.passbee.qnet.repository.QualificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QualificationService {

    private final QualificationRepository repository;
    private final QnetDataService qnetDataService; // QnetClient 의존성을 QnetDataService로 변경합니다.

    @Transactional
    public int importPage(int pageNo, int numOfRows) {
        // QnetDataService를 통해 API를 호출합니다.
        List<QualificationItem> items = qnetDataService.getQualifications(pageNo, numOfRows);

        if (items == null || items.isEmpty()) {
            return 0;
        }

        for (QualificationItem item : items) {
            // 이미 저장된 데이터인지 확인하고, 없으면 새로 저장합니다.
            repository.findById(item.getJmcd()).orElseGet(() -> {
                Qualification q = new Qualification();
                q.setJmcd(item.getJmcd());
                q.setJmfldnm(item.getJmfldnm());
                q.setMdobligfldcd(item.getMdobligfldcd());
                q.setMdobligfldnm(item.getMdobligfldnm());
                q.setObligfldcd(item.getObligfldcd());
                q.setObligfldnm(item.getObligfldnm());
                q.setQualgbcd(item.getQualgbcd());
                q.setQualgbnm(item.getQualgbnm());
                q.setSeriescd(item.getSeriescd());
                q.setSeriesnm(item.getSeriesnm());
                return repository.save(q);
            });
        }
        return items.size();
    }
}
