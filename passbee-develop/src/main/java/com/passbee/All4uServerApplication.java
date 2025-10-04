package com.passbee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan // 이 어노테이션을 추가합니다.
public class All4uServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(All4uServerApplication.class, args);
	}

}