package com.passbee.auth;

import com.passbee.auth.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "로그인/회원가입 (임시 스텁)")
public class AuthController {

    @PostMapping("/login")
    @Operation(
            summary = "로그인 (임시)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "예시",
                                    value = """
                      {"email":"test@example.com","password":"pass1234"}
                  """)
                    )
            )
    )
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid LoginRequest req) {
        // TODO: 추후 실제 검증 + JWT 발급
        var token = "dummy-" + UUID.randomUUID();
        return ResponseEntity.ok(new TokenResponse(token, "임시사용자", req.email()));
    }

    @PostMapping("/signup")
    @Operation(
            summary = "회원가입 (임시)",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(name = "예시",
                                    value = """
                      {"name":"홍길동","email":"gildong@example.com","password":"pass1234"}
                  """)
                    )
            )
    )
    public ResponseEntity<Map<String, Object>> signup(@RequestBody @Valid SignupRequest req) {
        // TODO: 추후 DB 저장 + 중복 체크
        return ResponseEntity.ok(Map.of(
                "message", "회원가입(임시) 성공",
                "user", Map.of("name", req.name(), "email", req.email())
        ));
    }
}
