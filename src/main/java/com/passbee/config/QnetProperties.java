package com.passbee.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "external.qnet")
public class QnetProperties {
    private String baseUrl;                 // application-*.yml: external.qnet.base-url
    private String serviceKey;              // application-*.yml: external.qnet.service-key
    private Map<String, String> endpoints;  // application-*.yml: external.qnet.endpoints.*
}