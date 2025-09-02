package com.all4u.all4u_server.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate(XmlMapper xmlMapper) {
        // JDK HttpURLConnection 사용 – 타임아웃 직접 지정
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(10).toMillis());

        RestTemplate rt = new RestTemplate(factory);

        // XML 응답을 Jackson XML로 파싱하도록 컨버터 추가
        MappingJackson2XmlHttpMessageConverter xmlConv =
                new MappingJackson2XmlHttpMessageConverter(xmlMapper);
        // XML을 우선 처리하도록 앞으로 추가(필수는 아니지만 안전)
        rt.getMessageConverters().add(0, xmlConv);

        return rt;
    }

    @Bean
    public XmlMapper xmlMapper() {
        // 알 수 없는 필드가 있어도 실패하지 않도록 설정
        XmlMapper mapper = new XmlMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }
}
