package com.neoproject.statement.exception;

import com.neoproject.statement.model.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StatementControllerAdvice {

    @ExceptionHandler(StatementDatabaseNotFoundException.class)
    ResponseEntity<ErrorMessage> handleStatementDatabaseNotFoundException(StatementDatabaseNotFoundException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(StatementExternalServiceException.class)
    ResponseEntity<ErrorMessage> handleStatementExternalServiceException(StatementExternalServiceException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorMessage);
    }
}
