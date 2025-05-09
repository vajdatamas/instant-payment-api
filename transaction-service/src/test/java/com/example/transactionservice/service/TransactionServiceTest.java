package com.example.transactionservice.service;

import com.example.dao.model.Account;
import com.example.dao.model.Transaction;
import com.example.dao.repository.TransactionRepository;
import com.example.transactionservice.exception.DuplicateTransactionException;
import com.example.transactionservice.exception.InsufficientBalanceException;
import com.example.transactionservice.exception.InvalidTransactionException;
import com.example.transactionservice.resource.request.TransactionRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountService accountService;

    @Mock
    private OutboxEventService outboxEventService;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_shouldSucceed_whenValidRequest() {
        final var transactionId = UUID.randomUUID();
        final var request = TransactionRequest.builder()
                .transactionId(transactionId)
                .amount(BigDecimal.valueOf(100))
                .senderAccount("123")
                .receiverAccount("456")
                .build();

        final var sender = new Account()
                .setId(1L)
                .setBalance(BigDecimal.valueOf(200));
        final var receiver = new Account()
                .setId(2L)
                .setBalance(BigDecimal.valueOf(150));

        final var transaction = new Transaction().setId(1L);

        when(transactionRepository.existsByTransactionId(any())).thenReturn(false);
        when(accountService.getByAccountNumber("123")).thenReturn(sender);
        when(accountService.getByAccountNumber("456")).thenReturn(receiver);
        when(transactionRepository.save(any())).thenReturn(transaction);

        final var response = transactionService.createTransaction(request);

        assertThat(response.message()).isEqualTo("Transaction completed.");
        verify(accountService).updateBalances(sender, receiver, BigDecimal.valueOf(100));
        verify(outboxEventService).saveTransactionCreatedEvent(response.transactionId());
    }

    @Test
    void createTransaction_shouldThrow_whenDuplicateTransactionId() {
        final var transactionId = UUID.randomUUID();
        final var request = TransactionRequest.builder()
                .transactionId(transactionId)
                .amount(BigDecimal.valueOf(100))
                .senderAccount("123")
                .receiverAccount("456")
                .build();

        when(transactionRepository.existsByTransactionId(any())).thenReturn(true);

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(DuplicateTransactionException.class)
                .hasMessageContaining("Transaction is already processed with id");

        verifyNoInteractions(accountService, outboxEventService);
    }

    @Test
    void createTransaction_shouldThrow_whenSameSenderAndReceiver() {
        final var transactionId = UUID.randomUUID();
        final var request = TransactionRequest.builder()
                .transactionId(transactionId)
                .amount(BigDecimal.valueOf(100))
                .senderAccount("123")
                .receiverAccount("123")
                .build();

        final var sender = new Account()
                .setId(1L)
                .setBalance(BigDecimal.valueOf(200));

        when(transactionRepository.existsByTransactionId(any())).thenReturn(false);
        when(accountService.getByAccountNumber("123")).thenReturn(sender);
        when(accountService.getByAccountNumber("123")).thenReturn(sender);

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Sender and receiver cannot be the same account.");

        verifyNoMoreInteractions(transactionRepository, accountService);
        verifyNoInteractions(outboxEventService);
    }

    @Test
    void createTransaction_shouldThrow_whenInsufficientBalance() {
        final var transactionId = UUID.randomUUID();
        final var request = TransactionRequest.builder()
                .transactionId(transactionId)
                .amount(BigDecimal.valueOf(300))
                .senderAccount("123")
                .receiverAccount("456")
                .build();

        final var sender = new Account()
                .setId(1L)
                .setBalance(BigDecimal.valueOf(200));
        final var receiver = new Account()
                .setId(2L)
                .setBalance(BigDecimal.valueOf(150));

        when(transactionRepository.existsByTransactionId(any())).thenReturn(false);
        when(accountService.getByAccountNumber("123")).thenReturn(sender);
        when(accountService.getByAccountNumber("456")).thenReturn(receiver);

        assertThatThrownBy(() -> transactionService.createTransaction(request))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Insufficient balance");

        verify(transactionRepository, never()).save(any());
    }
}
