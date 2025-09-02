package com.all4u.all4u_server.qnet.service;

import com.all4u.all4u_server.qnet.client.QnetClient;
import com.all4u.all4u_server.qnet.dto.common.QnetXmlBase;
import com.all4u.all4u_server.qnet.dto.stat.GradPiExamItem;
import com.all4u.all4u_server.stat.entity.StatGradePiExam;
import com.all4u.all4u_server.stat.repository.StatGradePiExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class StatGradeService {
    private final QnetClient qnetClient;
    private final StatGradePiExamRepository repo;

    @Value("${external.qnet.stat-base-url}") private String statBase;

    @Transactional
    public int importGradPiExam(int baseYear) {
        URI uri = URI.create(String.format(
                "%s/getGradPiExamList?baseYY=%d&%s", statBase, baseYear, qnetClient.keyParam()
        ));
        QnetXmlBase<GradPiExamItem> resp = qnetClient.get(uri, GradPiExamItem.class);

        int count = 0;
        for (var item : resp.getBody().getItems().getItem()) {
            var row = StatGradePiExam.builder()
                    .baseYear(baseYear)
                    .gradeName(item.getGradename())
                    .y1(item.getStatisyy1()).y2(item.getStatisyy2()).y3(item.getStatisyy3())
                    .y4(item.getStatisyy4()).y5(item.getStatisyy5()).y6(item.getStatisyy6())
                    .build();
            repo.save(row);
            count++;
        }
        return count;
    }
}
