package com.neoproject.calculator.exception;

import com.neoproject.calculator.model.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CalculatorControllerAdvice {

    @ExceptionHandler(ScoringException.class)
    ResponseEntity<ErrorMessage> handleScoringException(ScoringException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
