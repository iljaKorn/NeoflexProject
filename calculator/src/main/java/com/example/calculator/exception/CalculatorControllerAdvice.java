package com.example.calculator.exception;

import com.example.calculator.model.error.ErrorMessage;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class CalculatorControllerAdvice {

    @ExceptionHandler(PreScoringException.class)
    ErrorMessage handlePreScoringException(PreScoringException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(e.getMessage());
        return errorMessage;
    }

    @ExceptionHandler(ScoringException.class)
    ErrorMessage handleScoringException(ScoringException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(e.getMessage());
        return errorMessage;
    }
}
