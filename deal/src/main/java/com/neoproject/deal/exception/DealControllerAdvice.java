package com.neoproject.deal.exception;

import com.neoproject.deal.model.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DealControllerAdvice {

    @ExceptionHandler(DealDatabaseNotFoundException.class)
    ResponseEntity<ErrorMessage> handleDealDatabaseNotFoundException(DealDatabaseNotFoundException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(DealExternalServiceException.class)
    ResponseEntity<ErrorMessage> handleDealExternalServiceException(DealExternalServiceException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorMessage);
    }

    @ExceptionHandler(InvalidSesCodeException.class)
    ResponseEntity<ErrorMessage> handleInvalidSesCodeException(InvalidSesCodeException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }
}
