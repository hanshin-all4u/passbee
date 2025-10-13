package com.passbee.agency;

import com.passbee.qnet.dto.AgencyItem;
import com.passbee.qnet.service.QnetDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AgencyService {

    private final AgencyRepository agencyRepository;
    private final QnetDataService qnetDataService;

    @Transactional
    public int importAll(int pageSize) throws InterruptedException {
        agencyRepository.deleteAllInBatch();
        int pageNo = 1;
        int total = 0;
        while (true) {
            List<AgencyItem> items = qnetDataService.getAgencies(pageNo, pageSize);
            if (items == null || items.isEmpty()) {
                break;
            }
            for (AgencyItem item : items) {
                if (!agencyRepository.existsById(item.getRcogInstiCd())) {
                    Agency a = Agency.builder()
                            .rcogInstiCd(item.getRcogInstiCd())
                            .rcogInstiNm(item.getRcogInstiNm())
                            .crerRcogRate(item.getCrerRcogRate())
                            .validTermStartDt(item.getValidTermStartDt())
                            .validTermEndDt(item.getValidTermEndDt())
                            .build();
                    agencyRepository.save(a);
                }
            }
            total += items.size();
            pageNo++;
            Thread.sleep(300);
        }
        log.info("Imported agencies: {}", total);
        return total;
    }
}


