package com.passbee.config;

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

    // 나머지만 500으로
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> any(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", Map.of("code", "INTERNAL_ERROR", "message", e.getMessage())
        ));
    }
}

