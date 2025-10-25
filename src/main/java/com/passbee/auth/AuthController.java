package com.passbee.auth;

import com.passbee.auth.dto.LoginRequest;
import com.passbee.auth.dto.SignupRequest;
import com.passbee.auth.dto.TokenResponse;
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Parameter; // ▼▼▼ [추가] Parameter import
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping; // ▼▼▼ [추가] GetMapping import
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; // ▼▼▼ [추가] RequestParam import
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "로그인/회원가입")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsersRepository usersRepository; // 로그인 성공 시 사용자 정보 조회 위해 유지

    @PostMapping(value = "/login", consumes = "application/json")
    @Operation(
            summary = "로그인",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "예시",
                                    value = "{\"email\":\"test@example.com\",\"password\":\"pass1234\"}"
                            )
                    )
            )
    )
    public ResponseEntity<?> login(@org.springframework.web.bind.annotation.RequestBody @Valid LoginRequest req) {
        try {
            String token = authService.login(req);
            Users user = usersRepository.findByEmail(req.email())
                    .orElseThrow(() -> new IllegalStateException("인증 성공 후 사용자를 찾을 수 없습니다."));
            TokenResponse tokenResponse = new TokenResponse(token, user.getName(), user.getEmail());
            return ResponseEntity.ok(tokenResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    @Operation(
            summary = "회원가입",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            // ▼▼▼ [수정] Swagger 예시에 nickname 추가 ▼▼▼
                            examples = @ExampleObject(name = "예시",
                                    value = "{\"name\":\"홍길동\",\"nickname\":\"gildong123\",\"email\":\"gildong@example.com\",\"password\":\"pass1234\"}"
                            )
                    )
            )
    )
    public ResponseEntity<Map<String, Object>> signup(@org.springframework.web.bind.annotation.RequestBody @Valid SignupRequest req) {
        try {
            authService.signup(req);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "회원가입이 성공적으로 완료되었습니다.",
                    // ▼▼▼ [수정] 응답에 nickname도 포함 (선택 사항) ▼▼▼
                    "user", Map.of("name", req.name(), "nickname", req.nickname(), "email", req.email())
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }

    // ▼▼▼ [추가] 닉네임 중복 확인 API 엔드포인트 ▼▼▼
    @GetMapping("/check-nickname")
    @Operation(summary = "닉네임 중복 확인", description = "입력된 닉네임의 사용 가능 여부를 확인합니다.")
    public ResponseEntity<Map<String, Object>> checkNicknameAvailability(
            @Parameter(description = "중복 확인할 닉네임", required = true) @RequestParam String nickname) {

        boolean isAvailable = authService.isNicknameAvailable(nickname);

        if (isAvailable) {
            // 사용 가능 시 200 OK
            return ResponseEntity.ok(Map.of(
                    "available", true,
                    "message", "사용 가능한 닉네임입니다."
            ));
        } else {
            // 중복 또는 유효하지 않을 시 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "available", false,
                    "message", "이미 사용 중이거나 유효하지 않은 닉네임입니다."
            ));
        }
    }
}