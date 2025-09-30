package com.passbee.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Passbee API", version = "0.1.0", description = "시험 정보 API"))
public class OpenApiConfig {}
