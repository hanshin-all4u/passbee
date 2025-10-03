package com.passbee.qnet.client;

import com.passbee.config.QnetProperties;
import com.passbee.qnet.dto.common.QnetXmlBase;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class QnetClient {
    private final XmlMapper xmlMapper;
    private final QnetProperties qnetProperties; // @Value 대신 QnetProperties를 주입받습니다.

    public <T> QnetXmlBase<T> get(String endpoint, Map<String, String> params, Class<T> itemClass) {
        String responseBody = "";
        try {
            // 파라미터를 URL 쿼리 스트링으로 변환
            String queryParams = params.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + encode(entry.getValue()))
                    .collect(Collectors.joining("&"));

            String urlString = String.format("%s/%s?ServiceKey=%s&%s",
                    qnetProperties.getBaseUrl(), // 프로퍼티 클래스에서 값을 가져옵니다.
                    endpoint,
                    encode(qnetProperties.getServiceKey()), // 프로퍼티 클래스에서 값을 가져옵니다.
                    queryParams);

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/xml");

            int responseCode = conn.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                log.error("HTTP GET request failed for URL: {} with response code: {}", urlString, responseCode);
                return null;
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                responseBody = br.lines().collect(Collectors.joining(System.lineSeparator()));
            }
            conn.disconnect();

            if (responseBody.isEmpty()) {
                log.warn("Empty response body for URL: {}", urlString);
                return null;
            }

            return xmlMapper.readValue(responseBody,
                    xmlMapper.getTypeFactory().constructParametricType(QnetXmlBase.class, itemClass));

        } catch (Exception e) {
            log.error("Failed to process Q-net response for endpoint: {}. Body: {}", endpoint, responseBody, e);
            throw new IllegalStateException("Q-net 응답 처리 실패: " + e.getMessage(), e);
        }
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
