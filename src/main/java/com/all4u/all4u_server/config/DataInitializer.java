// 데이터 수집 예약 시간(매일 자정)을 무시하고 애플리케이션이 시작되자마자 데이터 수집을 한 번만 시행하도록 하는 코드
// 테스트 종료 후 삭제 또는 주석 처리

package com.all4u.all4u_server.config;

import com.all4u.all4u_server.scheduler.ScheduledDataCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ScheduledDataCollector scheduledDataCollector;

    @Override
    public void run(String... args) throws Exception {
        // 애플리케이션 시작 시 데이터 수집 로직을 1회 실행합니다.
        scheduledDataCollector.collectAllData();
    }
}