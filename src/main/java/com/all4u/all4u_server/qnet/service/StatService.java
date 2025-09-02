package com.all4u.all4u_server.qnet.service;

import com.all4u.all4u_server.qnet.client.QnetClient;
import com.all4u.all4u_server.qnet.dto.common.QnetXmlBase;
import com.all4u.all4u_server.qnet.dto.stat.TotalExamItem;
import com.all4u.all4u_server.stat.entity.StatTotal;
import com.all4u.all4u_server.stat.repository.StatTotalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class StatService {
    private final QnetClient qnetClient;
    private final StatTotalRepository totalRepo;

    @Value("${external.qnet.stat-base-url}") private String statBase;

    @Transactional
    public int importTotal(int baseYear) {
        URI uri = URI.create(String.format(
                "%s/getTotExamList?baseYY=%d&%s", statBase, baseYear, qnetClient.keyParam()
        ));
        QnetXmlBase<TotalExamItem> resp = qnetClient.get(uri, TotalExamItem.class);

        var item = resp.getBody().getItems().getItem().getFirst(); // 1건만 옴
        StatTotal row = new StatTotal();
        row.setBaseYear(baseYear);

        // 필드 매핑
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
}