// 데이터 수집 예약 시간(매일 자정)을 무시하고 애플리케이션이 시작되자마자 데이터 수집을 한 번만 시행하도록 하는 코드
// 테스트 종료 후 삭제 또는 주석 처리

package com.passbee.config;

import com.passbee.auth.AuthService;
// import com.passbee.auth.dto.SignupRequest; // <-- 이 줄은 이제 필요 없습니다.
import com.passbee.scheduler.ScheduledDataCollector;
import com.passbee.user.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component; // <-- 이 파일이 컴파일되므로 @Component가 활성화된 것으로 가정합니다.

@Component // <-- 이 파일이 컴파일되므로 @Component가 활성화된 것으로 가정합니다.
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

                // ▼▼▼ [수정] SignupRequest DTO 대신 register 메서드를 직접 호출합니다. ▼▼▼
                authService.register(
                        "test@example.com", // email
                        "pass1234",         // rawPw (비밀번호)
                        "테스트사용자",       // name (이름)
                        "테스트닉네임",       // nickname (닉네임)
                        null,               // phone (전화번호, 선택 사항)
                        null                // address (주소, 선택 사항)
                );

                // 테스트 사용자 생성 완료: test@example.com / pass1234
            }
            // 테스트 사용자가 이미 존재합니다: test@example.com
        } catch (Exception e) {
            // 테스트 사용자 생성 중 오류: " + e.getMessage()
        }
    }
}