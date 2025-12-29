package com.abra.revaissue.controller;

import com.abra.revaissue.exception.UnauthorizedOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(UnauthorizedOperation.class)
    public ResponseEntity<String> handleUnauthorizedOperation(UnauthorizedOperation exception){
        return ResponseEntity.status(403).body(exception.getMessage());
    }
}
