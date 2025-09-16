package com.all4u.all4u_server.qnet.service;

import com.all4u.all4u_server.qnet.client.QnetClient;
import com.all4u.all4u_server.qnet.dto.common.QnetXmlBase;
import com.all4u.all4u_server.qnet.dto.stat.GradPiExamItem;
import com.all4u.all4u_server.qnet.dto.stat.TotalExamItem;
import com.all4u.all4u_server.stat.entity.StatGradePiExam;
import com.all4u.all4u_server.stat.entity.StatTotal;
import com.all4u.all4u_server.stat.repository.StatGradePiExamRepository;
import com.all4u.all4u_server.stat.repository.StatTotalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatService {
    private final QnetClient qnetClient;
    private final StatTotalRepository totalRepo;
    private final StatGradePiExamRepository statGradePiExamRepository;

    @Value("${external.qnet.base-url}")
    private String baseUrl;

    @Value("${external.qnet.endpoints.total-exam}")
    private String totalExamEndpoint;

    @Value("${external.qnet.endpoints.grad-pi-exam}")
    private String gradPiExamEndpoint;

    @Transactional
    public int importTotal(int baseYear) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/" + totalExamEndpoint)
                .queryParam("serviceKey", qnetClient.encode(qnetClient.keyParam().split("=")[1]))
                .queryParam("baseYY", baseYear)
                .build()
                .toUri();
        QnetXmlBase<TotalExamItem> resp = qnetClient.get(uri, TotalExamItem.class);

        if (!"00".equals(resp.getHeader().getResultCode())) {
            throw new IllegalStateException("Q-net API error: " + resp.getHeader().getResultMsg());
        }

        var item = resp.getBody().getItems().getItem().getFirst();
        StatTotal row = new StatTotal();
        row.setBaseYear(baseYear);

        row.setPilrccnt1(item.getPilrccnt1()); row.setPilrccnt2(item.getPilrccnt2());
        row.setPilrccnt3(item.getPilrccnt3()); row.setPilrccnt4(item.getPilrccnt4());
        row.setPilrccnt5(item.getPilrccnt5()); row.setPilrccnt6(item.getPilrccnt6());

        row.setSilrccnt1(item.getSilrccnt1()); row.setSilrccnt2(item.getSilrccnt2());
        row.setSilrccnt3(item.getSilrccnt3()); row.setSilrccnt4(item.getSilrccnt4());
        row.setSilrccnt5(item.getSilrccnt5()); row.setSilrccnt6(item.getSilrccnt6());

        row.setPilpscnt1(item.getPilpscnt1()); row.setPilpscnt2(item.getPilpscnt2());
        row.setPilpscnt3(item.getPilpscnt3()); row.setPilpscnt4(item.getPilpscnt4());
        row.setPilpscnt5(item.getPilpscnt5()); row.setPilpscnt6(item.getPilpscnt6());

        row.setSilpacnt1(item.getSilpacnt1()); row.setSilpacnt2(item.getSilpacnt2());
        row.setSilpacnt3(item.getSilpacnt3()); row.setSilpacnt4(item.getSilpacnt4());
        row.setSilpacnt5(item.getSilpacnt5()); row.setSilpacnt6(item.getSilpacnt6());

        totalRepo.save(row);
        return 1;
    }

    @Transactional
    public int importGradPiExam(int baseYear) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/" + gradPiExamEndpoint)
                .queryParam("serviceKey", qnetClient.encode(qnetClient.keyParam().split("=")[1]))
                .queryParam("baseYY", baseYear)
                .build()
                .toUri();

        QnetXmlBase<GradPiExamItem> resp = qnetClient.get(uri, GradPiExamItem.class);

        if (!"00".equals(resp.getHeader().getResultCode())) {
            throw new IllegalStateException("Q-net API error: " + resp.getHeader().getResultMsg());
        }

        int count = 0;
        if (resp.getBody() != null && resp.getBody().getItems() != null && resp.getBody().getItems().getItem() != null) {
            for (GradPiExamItem item : resp.getBody().getItems().getItem()) {
                var row = StatGradePiExam.builder()
                        .baseYear(baseYear)
                        .gradeName(item.getGradename())
                        .y1(item.getStatisyy1()).y2(item.getStatisyy2()).y3(item.getStatisyy3())
                        .y4(item.getStatisyy4()).y5(item.getStatisyy5()).y6(item.getStatisyy6())
                        .build();
                statGradePiExamRepository.save(row);
                count++;
            }
        }
        return count;
    }
}
