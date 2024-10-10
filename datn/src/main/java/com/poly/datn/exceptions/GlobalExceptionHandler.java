package com.poly.datn.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler(Exception.class)
    // public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
    // Map<String, Object> response = new HashMap<>();
    // response.put("message", "Có lỗi xảy ra");
    // response.put("error", ex.getMessage());
    // return
    // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    // }

    @ExceptionHandler(IllegalUpdateException.class)
    public ResponseEntity<ApiError> handleIllegalUpdateException(IllegalUpdateException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), "Lỗi cập nhật", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiError> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN.value(), "Lỗi quyền truy cập", ex.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Bạn không có quyền thực hiện chức năng này.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Lỗi hệ thống", e.getMessage());
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException e) {
        Map<String, String> response = new HashMap<>();
        response.put("message", e.getMessage());
        return ResponseEntity.badRequest().body(response);
    }
}
