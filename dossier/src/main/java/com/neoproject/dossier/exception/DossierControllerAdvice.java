package com.neoproject.dossier.exception;

import com.neoproject.dossier.model.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class DossierControllerAdvice {

    @ExceptionHandler(DossierEmailSendException.class)
    ResponseEntity<ErrorMessage> handleDossierEmailSendException(DossierEmailSendException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @ExceptionHandler(DossierExternalServiceException.class)
    ResponseEntity<ErrorMessage> handleDossierExternalServiceException(DossierExternalServiceException e) {
        ErrorMessage errorMessage = new ErrorMessage();
        errorMessage.setDescription(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }
}
