package com.example.transactionservice.resource.response;

import lombok.Builder;

@Builder
public record ErrorResponse(
        String code,
        int status,
        String message
) {
}
