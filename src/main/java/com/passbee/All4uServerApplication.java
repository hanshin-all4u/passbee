package com.passbee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// ↓↓↓ 바로 이 한 줄! '배우 주소록'을 알려주는 import 입니다. ↓↓↓
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // JPA Auditing(생성일/수정일 자동화) 활성화
// ↓↓↓ 그리고 바로 이 한 줄! 총감독에게 배우들이 사는 동네 주소를 알려줍니다. ↓↓↓
@EntityScan(basePackages = "com.passbee")
@SpringBootApplication
public class All4uServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(All4uServerApplication.class, args);
	}
}
