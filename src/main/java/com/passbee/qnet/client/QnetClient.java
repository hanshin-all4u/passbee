package com.passbee.qnet.client;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.databind.JavaType;
import com.passbee.config.QnetProperties;
import com.passbee.qnet.dto.common.QnetXmlBase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringJoiner;

@Slf4j
@Component
@RequiredArgsConstructor
public class QnetClient {

    private final QnetProperties qnetProperties;
    private final XmlMapper xmlMapper = new XmlMapper();

    /** endpointPath: 프로퍼티에서 꺼낸 상대 경로 (예: InquiryStatSVC/getTotExamList) */
    public <T> QnetXmlBase<T> get(String endpointPath, Map<String,String> params, Class<T> itemClass) {
        try {
            String url = buildUrl(
                    qnetProperties.getBaseUrl(),
                    endpointPath,
                    qnetProperties.getServiceKey(),
                    params
            );
            log.debug("[QNET] GET {}", url);

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(20000);

            int code = conn.getResponseCode();
            if (code != 200) {
                log.error("[QNET] Non-200 response: {}", code);
                throw new IllegalStateException("QNET HTTP " + code);
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line; while ((line = br.readLine()) != null) sb.append(line);
            }
            String xml = sb.toString();

            // QnetXmlBase<T> 제네릭 타입 구성
            JavaType type = xmlMapper.getTypeFactory()
                    .constructParametricType(QnetXmlBase.class, itemClass);

            return xmlMapper.readValue(xml, type);

        } catch (Exception e) {
            log.error("[QNET] request failed: {}", e.getMessage(), e);
            return null;
        }
    }

    private static String buildUrl(String baseUrl, String endpoint, String serviceKey, Map<String,String> params) throws Exception {
        StringJoiner sj = new StringJoiner("&");
        sj.add("ServiceKey=" + URLEncoder.encode(serviceKey, StandardCharsets.UTF_8));
        if (params != null) {
            for (var e : params.entrySet()) {
                sj.add(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "="
                        + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
            }
        }
        String b = baseUrl.replaceAll("/+$","");
        String ep = endpoint.replaceAll("^/+","");
        return b + "/" + ep + "?" + sj;
    }
}
