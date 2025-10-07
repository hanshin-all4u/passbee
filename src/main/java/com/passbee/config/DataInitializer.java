// 데이터 수집 예약 시간(매일 자정)을 무시하고 애플리케이션이 시작되자마자 데이터 수집을 한 번만 시행하도록 하는 코드
// 테스트 종료 후 삭제 또는 주석 처리

package com.passbee.config;

import com.passbee.auth.AuthService;
import com.passbee.auth.dto.SignupRequest;
import com.passbee.scheduler.ScheduledDataCollector;
import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ScheduledDataCollector scheduledDataCollector;
    private final AuthService authService;
    private final UsersRepository usersRepository;

    @Override
    public void run(String... args) throws Exception {
        // 테스트 사용자 생성
        createTestUser();
        
        // 애플리케이션 시작 시 데이터 수집 로직을 1회 실행합니다.
        scheduledDataCollector.collectAllData();
    }
    
    private void createTestUser() {
        try {
            // 테스트 사용자가 이미 존재하는지 확인
            if (!usersRepository.existsByEmail("test@example.com")) {
                SignupRequest testUser = new SignupRequest("테스트사용자", "test@example.com", "pass1234");
                authService.signup(testUser);
                System.out.println("테스트 사용자 생성 완료: test@example.com / pass1234");
            } else {
                System.out.println("테스트 사용자가 이미 존재합니다: test@example.com");
            }
        } catch (Exception e) {
            System.out.println("테스트 사용자 생성 중 오류: " + e.getMessage());
        }
    }
}