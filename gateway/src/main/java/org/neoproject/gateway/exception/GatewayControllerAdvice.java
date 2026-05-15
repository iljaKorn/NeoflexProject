package org.neoproject.gateway.exception;

import org.neoproject.gateway.model.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GatewayControllerAdvice {

    @ExceptionHandler(GatewayExternalServiceException.class)
    ResponseEntity<ErrorMessage> handleGatewayExternalServiceException(GatewayExternalServiceException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(e.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorMessage);
    }
}
