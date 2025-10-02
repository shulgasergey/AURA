package com.shulha.betapsycho.exception;

/**
 * This source code and all associated intellectual property
 * are the exclusive property of the author.
 * --------------------------------------------------
 * Unauthorized copying, modification, distribution, or derivative
 * works without prior written consent is strictly prohibited
 * and may be prosecuted to the fullest extent of the law.
 * --------------------------------------------------
 * Copyright © 2025 BetaPsycho Serhii Shulha.
 */

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import com.shulha.betapsycho.exception.customException.TooManyRequestsException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("message", ex.getMessage());

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<Map<String, Object>> handleTooManyRequests(TooManyRequestsException ex) {
        log.warn("Too many requests: {}", ex.getMessage());
        HttpStatus status = HttpStatus.TOO_MANY_REQUESTS;

        Map<String, Object> response = new HashMap<>();
        response.put("error", Map.of("message", ex.getMessage()));

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, Object> response = new HashMap<>();
        response.put("error", Map.of("message", ex.getMessage()));

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFound(jakarta.persistence.EntityNotFoundException ex) {
        log.warn("Entity not found: {}", ex.getMessage());
        HttpStatus status = HttpStatus.NOT_FOUND;

        Map<String, Object> response = new HashMap<>();
        response.put("error", Map.of("message", ex.getMessage()));

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        HttpStatus status = HttpStatus.FORBIDDEN;

        Map<String, Object> response = new HashMap<>();
        response.put("error", Map.of("message", ex.getMessage()));

        return new ResponseEntity<>(response, status);
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(org.springframework.web.bind.MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, Object> response = new HashMap<>();
        response.put("error", Map.of(
                "message", "Validation failed",
                "details", ex.getBindingResult().getFieldErrors().stream()
                        .map(f -> f.getField() + ": " + f.getDefaultMessage())
                        .toList()
        ));

        return new ResponseEntity<>(response, status);
    }
}