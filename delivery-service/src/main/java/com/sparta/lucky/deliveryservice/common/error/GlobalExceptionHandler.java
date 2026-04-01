package com.sparta.lucky.deliveryservice.common.error;

import com.sparta.lucky.deliveryservice.common.error.exceptions.ConflictException;
import com.sparta.lucky.deliveryservice.common.error.exceptions.ForbiddenException;
import com.sparta.lucky.deliveryservice.common.error.exceptions.NotFoundException;
import com.sparta.lucky.deliveryservice.common.response.CommonApiResponse;
import com.sparta.lucky.deliveryservice.common.response.ResponseCode;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Not found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CommonApiResponse<String>> notFoundExceptionHandler(NotFoundException ex) {
        return ResponseEntity
            .status(ex.code().status())
            .body(CommonApiResponse.error(ResponseCode.NOT_FOUND, ex.getMessage()));
    }

    // Conflict
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<CommonApiResponse<String>> conflictExceptionHandler(ConflictException ex) {
        return ResponseEntity
            .status(ex.code().status())
            .body(CommonApiResponse.error(ex.code()));
    }

    // Forbidden
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CommonApiResponse<String>>  authorizationDeniedHandler(
        ForbiddenException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(CommonApiResponse.error(ResponseCode.FORBIDDEN));
    }

    // Request - unexpected api errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonApiResponse<Map<String, String>>>
    validatorHandler(MethodArgumentNotValidException ex) {
        log.warn("[WARN] MethodArgumentNotValidException :: {}",ex.getMessage(), ex);
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getFieldErrors().forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity
            .badRequest()
            .body(CommonApiResponse.error(ResponseCode.VALIDATION_ERROR, errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonApiResponse<String>> notReadableHandler(HttpMessageNotReadableException ex) {
        log.warn("[WARN] HttpMessageNotReadableException :: {}", ex.getMessage(), ex);
        return ResponseEntity
            .badRequest()
            .body(CommonApiResponse.error(ResponseCode.NOT_READABLE, "Incorrect request body"));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<CommonApiResponse<String>> missingRequestHeaderHandler(MissingRequestHeaderException ex) {
        log.warn("[WARN] MissingRequestHeaderException :: {}", ex.getMessage());
        return ResponseEntity
            .badRequest()
            .body(CommonApiResponse.error(ResponseCode.MISSING_HEADER, "Incorrect request header"));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<CommonApiResponse<String>>
    methodNotSupportedHandler(HttpRequestMethodNotSupportedException ex) {
        log.warn("[WARN] HttpRequestMethodNotSupportedException :: {}",ex.getMessage(), ex);
        return ResponseEntity
            .status(ResponseCode.METHOD_NOT_ALLOWED.status())
            .body(CommonApiResponse.error(ResponseCode.METHOD_NOT_ALLOWED, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonApiResponse<String>> exceptionHandler(Exception ex) {
        log.error("[ERROR] Exception :: {}", ex.getMessage(), ex);
        return ResponseEntity
            .internalServerError()
            .body(CommonApiResponse.error(ResponseCode.INTERNAL_ERROR, "Internal server error"));
    }
}
