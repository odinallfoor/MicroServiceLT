package com.cgarrido.microservicelt.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handlerAllExceptions(Exception ex){

        ApiError defaultError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .codigo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail(ex.getMessage())
                .build();

        Map<String, Object> responseErrorBody = new HashMap<>();
        responseErrorBody.put("error", Arrays.asList(defaultError));

        return new ResponseEntity<>(responseErrorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
