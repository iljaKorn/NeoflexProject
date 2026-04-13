package com.neoproject.statement.exception;

public class StatementDatabaseNotFoundException extends RuntimeException {
    public StatementDatabaseNotFoundException(String message) {
        super(message);
    }
}
