package org.neoproject.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class GatewayExternalServiceException extends RuntimeException {

    private final HttpStatus statusCode;

    public GatewayExternalServiceException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
