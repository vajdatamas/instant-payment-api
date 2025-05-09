package com.example.transactionservice.resource.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record TransactionRequest(

        @NotNull(message = "TransactionId is required")
        UUID transactionId,

        @NotNull(message = "Amount is required.")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero.")
        BigDecimal amount,

        @NotBlank(message = "Sender account is required.")
        String senderAccount,

        @NotBlank(message = "Receiver account is required.")
        String receiverAccount
) {
}
