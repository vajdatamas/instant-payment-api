package com.example.transactionservice.exception;

public class InvalidTransactionException extends RuntimeException {
    public InvalidTransactionException(final String message) {
        super(message);
    }
}
