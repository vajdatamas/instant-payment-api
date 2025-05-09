package com.example.transactionservice.exception;

import lombok.Getter;

@Getter
public class ResourceValidationException extends RuntimeException {
    private final String code;

    public ResourceValidationException(final String code, final String message) {
        super(message);
        this.code = code;
    }
}
