package com.passbee.user.web;

import com.passbee.user.dto.NicknameUpdateRequestDto;
import com.passbee.user.dto.PasswordUpdateRequestDto;
import com.passbee.user.dto.UserInfoResponseDto;
import com.passbee.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "MyPage", description = "마이페이지 (회원 정보) API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage") // 마이페이지 관련 경로는 /api/mypage로 시작
public class UserController {

    private final UserService userService;

    /**
     * 1. 내 정보 조회 (REQ-MEM-004)
     */
    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "로그인이 필요합니다."));
        }

        UserInfoResponseDto userInfo = userService.getUserInfo(userDetails.getUsername());
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 2. 닉네임 변경 (REQ-MEM-004)
     */
    @Operation(summary = "닉네임 변경")
    @PatchMapping("/nickname")
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
            // 닉네임 중복 시 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * 3. 비밀번호 변경 (REQ-MEM-004)
     */
    @Operation(summary = "비밀번호 변경")
    @PatchMapping("/password")
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
            // 현재 비밀번호 불일치 시 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // 새 비밀번호가 현재 비밀번호와 같을 때 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
        }
    }
}