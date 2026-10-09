package com.efeakif.intibak_control.exception;

import java.io.IOException;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException exception,
            HttpServletRequest httpServletRequest) {
        ErrorResponse errorResponse = ErrorResponse.builder().timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage())
                .path(httpServletRequest.getRequestURI()).build();

        return ResponseEntity.badRequest().body(errorResponse);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IllegalArgumentException exception,
            HttpServletRequest httpServletRequest) {
        ErrorResponse errorResponse = ErrorResponse.builder().timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()).message(exception.getMessage())
                .path(httpServletRequest.getRequestURI()).build();

        return ResponseEntity.badRequest().body(errorResponse);

    }

}
