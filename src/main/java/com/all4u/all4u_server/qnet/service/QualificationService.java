package com.all4u.all4u_server.qnet.service;

import com.all4u.all4u_server.qnet.dto.QnetResponse;
import com.all4u.all4u_server.qnet.dto.QualificationItem;
import com.all4u.all4u_server.qnet.entity.Qualification;
import com.all4u.all4u_server.qnet.repository.QualificationRepository;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QualificationService {

    private final QualificationRepository repository;
    private final RestTemplate restTemplate; // Config에서 주입
    private final XmlMapper xmlMapper;       // Config에서 주입

    // application.yml에서 주입 (기본값 포함)
    @Value("${external.qnet.base-url:http://openapi.q-net.or.kr/api/service/rest/InquiryListNationalQualifcationSVC}")
    private String baseUrl;

    // ⚠ 디코딩(원문) 키를 yml에 보관하고 주입받음
    @Value("${external.qnet.service-key}")
    private String serviceKey;

    @Transactional
    public int importPage(int pageNo, int numOfRows) throws Exception {
        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl + "/getList")
                .queryParam("serviceKey", serviceKey)   // 자동 URL 인코딩
                .queryParam("_type", "xml")
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .toUriString();

        String xml = restTemplate.getForObject(url, String.class);
        QnetResponse resp = xmlMapper.readValue(xml, QnetResponse.class);

        if (!"00".equals(resp.getHeader().getResultCode())) {
            throw new IllegalStateException("Q-net API error: " + resp.getHeader().getResultMsg());
        }

        List<Qualification> toSave = new ArrayList<>();
        if (resp.getBody() != null &&
                resp.getBody().getItems() != null &&
                resp.getBody().getItems().getItem() != null) {

            for (QualificationItem item : resp.getBody().getItems().getItem()) {
                Qualification q = new Qualification();
                q.setJmcd(item.getJmcd());                 // PK
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
