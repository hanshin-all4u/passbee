package com.passbee.config;

// ▼▼▼ [추가] 2개의 import 구문을 추가합니다. ▼▼▼
import com.passbee.common.exception.AuthorizationException;
import com.passbee.common.exception.ResourceNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404는 404로!
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> notFound(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", Map.of("code", "NOT_FOUND", "message", e.getMessage())
        ));
    }

    // 405도 분리(선택)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of(
                "error", Map.of("code", "METHOD_NOT_ALLOWED", "message", e.getMessage())
        ));
    }

    // ▼▼▼ [추가] ResourceNotFoundException 핸들러 (404) ▼▼▼
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", Map.of("code", "RESOURCE_NOT_FOUND", "message", e.getMessage())
        ));
    }

    // ▼▼▼ [추가] AuthorizationException 핸들러 (403) ▼▼▼
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<?> authorizationFailed(AuthorizationException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", Map.of("code", "FORBIDDEN", "message", e.getMessage())
        ));
    }

    // 나머지만 500으로
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> any(Exception e) {
        // ▼▼▼ [수정] 스택 트레이스 로깅 추가 (디버깅에 유용) ▼▼▼
        e.printStackTrace(); 
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", Map.of("code", "INTERNAL_ERROR", "message", e.getMessage())
        ));
    }
}