package com.learnkafka.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class LibraryEventControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> toHandleRequestBody(MethodArgumentNotValidException ex){
        List<FieldError> errorList = ex.getBindingResult().getFieldErrors();

        String errorMessage = errorList.stream()
                .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
                .sorted()  // sorted is for unit test case
                .collect(Collectors.joining(" && "));
        log.info("VALIDATION ERROR : {} ", errorMessage);
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);

    }

}
