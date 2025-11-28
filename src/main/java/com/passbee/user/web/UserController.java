package com.passbee.user.web;

import com.passbee.user.dto.NicknameUpdateRequestDto;
import com.passbee.user.dto.PasswordUpdateRequestDto;
import com.passbee.user.dto.UserInfoResponseDto;
import com.passbee.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement; // SecurityRequirement import
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// ▼▼▼ [수정] 태그 이름 및 경로 변경 ▼▼▼
@Tag(name = "Users (MyPage)", description = "사용자 정보 (마이페이지) API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users") // 경로를 /api/users 로 변경
public class UserController {

    private final UserService userService;

    /**
     * 1. 내 정보 조회
     */
    @Operation(summary = "내 정보 조회", security = @SecurityRequirement(name = "JWT TOKEN")) // 인증 필요 명시
    // ▼▼▼ [수정] 경로를 /me 로 변경 ▼▼▼
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            // Spring Security 설정으로 인해 이 코드는 거의 도달하지 않지만, 안전장치로 둡니다.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        UserInfoResponseDto userInfo = userService.getUserInfo(userDetails.getUsername());
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 2. 닉네임 변경
     */
    @Operation(summary = "닉네임 변경", security = @SecurityRequirement(name = "JWT TOKEN")) // 인증 필요 명시
    // ▼▼▼ [수정] 경로를 /me/nickname 으로 변경 (또는 PUT /me 로 통합 고려) ▼▼▼
    @PatchMapping("/me/nickname")
    public ResponseEntity<?> updateNickname(
            @Valid @RequestBody NicknameUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            UserInfoResponseDto updatedUser = userService.updateNickname(userDetails.getUsername(), requestDto);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 3. 비밀번호 변경
     */
    @Operation(summary = "비밀번호 변경", security = @SecurityRequirement(name = "JWT TOKEN")) // 인증 필요 명시
    // ▼▼▼ [수정] 경로를 /me/password 로 변경 (또는 PUT /me 로 통합 고려) ▼▼▼
    @PatchMapping("/me/password")
    public ResponseEntity<?> updatePassword(
            @Valid @RequestBody PasswordUpdateRequestDto requestDto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        try {
            userService.updatePassword(userDetails.getUsername(), requestDto);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 4. 회원 탈퇴 (신규 추가)
     */
    @Operation(summary = "회원 탈퇴", description = "현재 로그인된 사용자 계정을 삭제합니다.", security = @SecurityRequirement(name = "JWT TOKEN"))
    // ▼▼▼ [추가] 회원 탈퇴 엔드포인트 ▼▼▼
    @DeleteMapping("/me")
    public ResponseEntity<?> deleteAccount(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        userService.deleteUser(userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 성공적으로 처리되었습니다."));
    }
}