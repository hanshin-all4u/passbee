package com.passbee.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    // RestTemplate Bean은 삭제하고, XmlMapper Bean만 남겨둡니다.
    @Bean
    public XmlMapper xmlMapper() {
        // API 응답에 알 수 없는 필드가 있어도 파싱에 실패하지 않도록 설정합니다.
        XmlMapper mapper = new XmlMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}