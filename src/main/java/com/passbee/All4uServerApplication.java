package com.passbee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@EnableAsync
@EnableMethodSecurity
@SpringBootApplication(scanBasePackages = "com.passbee")
public class All4uServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(All4uServerApplication.class, args);
	}
}