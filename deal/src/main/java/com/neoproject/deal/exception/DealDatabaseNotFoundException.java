package com.neoproject.deal.exception;

public class DealDatabaseNotFoundException extends RuntimeException {
    public DealDatabaseNotFoundException(String message) {
        super(message);
    }
}
