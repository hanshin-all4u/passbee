package com.passbee.config; // 또는 com.all4u.all4u_server.config

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper; // import 추가
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary; // import 추가

@Configuration
public class RestClientConfig {

    // JSON 처리를 위한 ObjectMapper를 기본(Primary)으로 설정합니다.
    @Bean
    @Primary // 이 어노테이션이 핵심입니다.
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        // API 응답에 알 수 없는 필드가 있어도 파싱에 실패하지 않도록 설정합니다.
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    // Q-Net 통신을 위한 XmlMapper는 그대로 둡니다.
    @Bean
    public XmlMapper xmlMapper() {
        XmlMapper mapper = new XmlMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}