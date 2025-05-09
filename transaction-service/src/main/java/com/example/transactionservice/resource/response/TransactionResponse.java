package com.example.transactionservice.resource.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record TransactionResponse(
        UUID transactionId,
        String message
) {
}
