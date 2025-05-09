package com.example.transactionservice.exception;

public class DuplicateTransactionException extends RuntimeException {
    public DuplicateTransactionException(final String message) {
        super(message);
    }
}
