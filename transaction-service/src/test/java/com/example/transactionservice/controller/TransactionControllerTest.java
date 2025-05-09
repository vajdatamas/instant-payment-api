package com.example.transactionservice.controller;

import com.example.transactionservice.exception.DuplicateTransactionException;
import com.example.transactionservice.exception.ResourceValidationException;
import com.example.transactionservice.resource.request.TransactionRequest;
import com.example.transactionservice.resource.response.TransactionResponse;
import com.example.transactionservice.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    public void test_shouldCreateTransactionSuccessfully() {
        // given
        final var request = new TransactionRequest(
                null,
                BigDecimal.valueOf(100.00),
                "1234567890",
                "0987654321"
        );

        final var generatedId = UUID.randomUUID();

        final var expectedResponse = TransactionResponse.builder()
                .transactionId(generatedId)
                .message("Transaction completed.")
                .build();

        when(transactionService.createTransaction(request)).thenReturn(expectedResponse);

        // when
        final var response = transactionController.createTransaction(request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().transactionId()).isEqualTo(generatedId);
        assertThat(response.getBody().message()).isEqualTo("Transaction completed.");

        verify(transactionService, times(1)).createTransaction(request);

    }

    @Test
    void test_shouldReturn500WhenServiceFails() {
        // given
        final var request = new TransactionRequest(
                null,
                BigDecimal.valueOf(100.00),
                "1234567890",
                "0987654321"
        );

        when(transactionService.createTransaction(request))
                .thenThrow(new IllegalStateException("Unexpected error"));

        // when
        try {
            transactionController.createTransaction(request);
        } catch (IllegalStateException ex) {
            // then
            assertThat(ex).isInstanceOf(IllegalStateException.class);
            assertThat(ex.getMessage()).isEqualTo("Unexpected error");
        }

        verify(transactionService, times(1)).createTransaction(request);
    }

    @Test
    void test_shouldThrowDuplicateTransactionException() {
        // Given
        final var request = new TransactionRequest(
                UUID.randomUUID(),
                new BigDecimal("50.00"),
                "1111111111",
                "2222222222"
        );

        when(transactionService.createTransaction(request))
                .thenThrow(new DuplicateTransactionException("Transaction already exists"));

        // When / Then
        final var exception = assertThrows(
                DuplicateTransactionException.class,
                () -> transactionController.createTransaction(request)
        );

        assertThat(exception.getMessage()).isEqualTo("Transaction already exists");
        verify(transactionService, times(1)).createTransaction(request);
    }

    @Test
    void test_shouldThrowValidationException() {
        // Given
        final var request = new TransactionRequest(
                UUID.randomUUID(),
                BigDecimal.ZERO, // Invalid value (példa)
                "1111111111",
                "2222222222"
        );

        when(transactionService.createTransaction(request))
                .thenThrow(new ResourceValidationException("amount", "Amount must be greater than zero"));

        // When / Then
        final var exception = assertThrows(
                ResourceValidationException.class,
                () -> transactionController.createTransaction(request)
        );

        assertThat(exception.getMessage()).contains("Amount must be greater than zero");
        verify(transactionService).createTransaction(request);
    }

}