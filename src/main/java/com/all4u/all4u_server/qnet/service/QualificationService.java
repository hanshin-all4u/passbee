package com.all4u.all4u_server.qnet.service;

import com.all4u.all4u_server.qnet.client.QnetClient;
import com.all4u.all4u_server.qnet.dto.QualificationItem;
import com.all4u.all4u_server.qnet.dto.common.QnetXmlBase;
import com.all4u.all4u_server.qnet.entity.Qualification;
import com.all4u.all4u_server.qnet.repository.QualificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QualificationService {

    private final QualificationRepository repository;
    private final QnetClient qnetClient;

    @Value("${external.qnet.base-url}")
    private String baseUrl;

    @Value("${external.qnet.endpoints.qualifications}")
    private String qualificationsEndpoint;

    @Transactional
    public int importPage(int pageNo, int numOfRows) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/" + qualificationsEndpoint)
                .queryParam("serviceKey", qnetClient.encode(qnetClient.keyParam().split("=")[1]))
                .queryParam("_type", "xml")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .build()
                .toUri();

        QnetXmlBase<QualificationItem> resp = qnetClient.get(uri, QualificationItem.class);

        if (!"00".equals(resp.getHeader().getResultCode())) {
            throw new IllegalStateException("Q-net API error: " + resp.getHeader().getResultMsg());
        }

        List<Qualification> toSave = new ArrayList<>();
        if (resp.getBody() != null &&
                resp.getBody().getItems() != null &&
                resp.getBody().getItems().getItem() != null) {

            for (QualificationItem item : resp.getBody().getItems().getItem()) {
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
                toSave.add(q);
            }
        }
        repository.saveAll(toSave);
        return toSave.size();
    }
}
