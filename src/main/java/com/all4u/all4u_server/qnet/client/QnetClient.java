package com.all4u.all4u_server.qnet.client;

import com.all4u.all4u_server.qnet.dto.common.QnetXmlBase;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class QnetClient {
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper = new XmlMapper();

    @Value("${external.qnet.service-key}") private String serviceKey;

    public <T> QnetXmlBase<T> get(URI uri, Class<T> itemClass) {
        ResponseEntity<String> res = restTemplate.getForEntity(uri, String.class);
        try {
            // 공통 래퍼에 itemClass를 주입해도 되지만, 단순히 문자열→제네릭 래퍼로 바인딩
            // (Item 타입 필드는 런타임에 자동 매핑됨)
            return xmlMapper.readValue(res.getBody(),
                    xmlMapper.getTypeFactory().constructParametricType(QnetXmlBase.class, itemClass));
        } catch (Exception e) {
            throw new IllegalStateException("Q-net 응답 파싱 실패: " + e.getMessage(), e);
        }
    }

    public String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public String keyParam() {
        return "ServiceKey=" + encode(serviceKey);
    }
}