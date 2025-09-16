package com.all4u.all4u_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class All4uServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(All4uServerApplication.class, args);
	}

}
