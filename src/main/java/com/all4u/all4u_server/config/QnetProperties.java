package com.all4u.all4u_server.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "external.qnet")
@Getter
@Setter
public class QnetProperties {
    private String baseUrl;
    private String serviceKey;
    private Map<String, String> endpoints;
}