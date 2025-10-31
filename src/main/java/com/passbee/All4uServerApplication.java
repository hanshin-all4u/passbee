package com.passbee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan // ★ 없으면 추가
public class All4uServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(All4uServerApplication.class, args);
	}
}