package com.passbee.config;

import com.passbee.common.exception.AuthorizationException;
import com.passbee.common.exception.ResourceNotFoundException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpRequestMethodNotSupportedException; // 이 import는 이제 사용되지 않음
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException; // 이 import도 이제 사용되지 않음

import java.util.Map;
import java.util.HashMap;

@RestControllerAdvice
// ResponseEntityExceptionHandler를 상속받습니다.
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ▼▼▼ [삭제] 404 핸들러: 부모 클래스가 처리하므로 중복되어 삭제합니다. ▼▼▼
    /*
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> notFound(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", Map.of("code", "NOT_FOUND", "message", e.getMessage())
        ));
    }
    */

    // ▼▼▼ [삭제] 405 핸들러: 부모 클래스가 처리하므로 중복되어 삭제합니다. ▼▼▼
    /*
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> methodNotAllowed(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(Map.of(
                "error", Map.of("code", "METHOD_NOT_ALLOWED", "message", e.getMessage())
        ));
    }
    */

    // ▼▼▼ [유지] 우리가 직접 정의한 예외 (ResourceNotFoundException - 404) ▼▼▼
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "error", Map.of("code", "RESOURCE_NOT_FOUND", "message", e.getMessage())
        ));
    }

    // ▼▼▼ [유지] 우리가 직접 정의한 예외 (AuthorizationException - 403) ▼▼▼
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<?> authorizationFailed(AuthorizationException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "error", Map.of("code", "FORBIDDEN", "message", e.getMessage())
        ));
    }

    // ▼▼▼ [유지] @Valid 유효성 검사 실패 시 (400 Bad Request) ▼▼▼
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "error", Map.of("code", "VALIDATION_FAILED", "message", errors)
        ));
    }

    // ▼▼▼ [유지] 나머지 모든 예외 (500 Internal Server Error) ▼▼▼
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> any(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", Map.of("code", "INTERNAL_ERROR", "message", e.getMessage())
        ));
    }
}