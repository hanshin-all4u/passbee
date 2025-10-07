package com.passbee.auth;

import com.passbee.auth.dto.LoginRequest;
import com.passbee.auth.dto.SignupRequest;
import com.passbee.auth.dto.TokenResponse;
import com.passbee.user.Users;
import com.passbee.user.UsersRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "로그인/회원가입")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsersRepository usersRepository;

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
            // 1. AuthService로부터 실제 JWT(String)를 받아옵니다.
            String token = authService.login(req);

            // 2. 응답에 필요한 사용자 정보를 DB에서 다시 찾아옵니다.
            Users user = usersRepository.findByEmail(req.email())
                    .orElseThrow(() -> new IllegalStateException("인증 성공 후 사용자를 찾을 수 없습니다."));

            // 3. 성공 응답에 실제 토큰과 사용자 정보를 담아 반환합니다.
            TokenResponse tokenResponse = new TokenResponse(token, user.getName(), user.getEmail());
            return ResponseEntity.ok(tokenResponse);

        } catch (BadCredentialsException e) {
            // 4. 인증 실패 시 401 Unauthorized 상태 코드를 반환합니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping(value = "/signup", consumes = "application/json")
    @Operation(
            summary = "회원가입",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "예시",
                                    value = "{\"name\":\"홍길동\",\"email\":\"gildong@example.com\",\"password\":\"pass1234\"}"
                            )
                    )
            )
    )
    public ResponseEntity<Map<String, Object>> signup(@org.springframework.web.bind.annotation.RequestBody @Valid SignupRequest req) {
        try {
            // authService의 signup 메소드를 호출하여 사용자를 등록합니다.
            authService.signup(req);

            // 성공 시, 201 Created 상태 코드와 성공 메시지를 반환합니다.
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "회원가입이 성공적으로 완료되었습니다.",
                    "user", Map.of("name", req.name(), "email", req.email())
            ));
        } catch (IllegalArgumentException e) {
            // AuthService에서 이메일 중복 예외가 발생하면, 409 Conflict 상태 코드를 반환합니다.
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                    "message", e.getMessage()
            ));
        }
    }
}