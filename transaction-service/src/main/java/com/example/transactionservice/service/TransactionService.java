package com.example.transactionservice.service;

import com.example.dao.model.Account;
import com.example.dao.model.Transaction;
import com.example.dao.model.type.TransactionStatus;
import com.example.dao.repository.TransactionRepository;
import com.example.transactionservice.exception.DuplicateTransactionException;
import com.example.transactionservice.exception.InsufficientBalanceException;
import com.example.transactionservice.resource.request.TransactionRequest;
import com.example.transactionservice.resource.response.TransactionResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountService accountService;
    private final OutboxEventService outboxEventService;
    private final TransactionRepository transactionRepository;

    @Transactional
    public TransactionResponse createTransaction(final TransactionRequest transactionRequest) {
        final var transactionId = !Objects.isNull(transactionRequest.transactionId())
                ? transactionRequest.transactionId()
                : generateUniqueTransactionId();

        //exactly once check
        if (transactionRepository.existsByTransactionId(transactionId)) {
            throw new DuplicateTransactionException(String.format("Transaction is already processed with id: [ %s ]", transactionId.toString()));
        }

        final Account sender = accountService.getByAccountNumber(transactionRequest.senderAccount());
        final Account receiver = accountService.getByAccountNumber(transactionRequest.receiverAccount());
        final BigDecimal amount = transactionRequest.amount();

        if (!sender.hasSufficientBalance(amount)) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        // 3. Balance update
        accountService.updateBalances(sender, receiver, amount);

        // 4. Transaction creation
        createAndSaveTransaction(transactionRequest, transactionId, amount);

        // 5. Outbox event save
        outboxEventService.saveTransactionCreatedEvent(transactionId);

        return TransactionResponse.builder()
                .transactionId(transactionId)
                .message("Transaction completed.")
                .build();
    }

    private void createAndSaveTransaction(final TransactionRequest transactionRequest,
                                          final UUID transactionId,
                                          final BigDecimal amount) {
        log.info("Creating transaction. Incoming ID: {}", transactionId);
        final Transaction transaction = new Transaction();
        transaction.setTransactionId(transactionId);
        transaction.setAmount(amount);
        transaction.setSenderAccount(transactionRequest.senderAccount());
        transaction.setReceiverAccount(transactionRequest.receiverAccount());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setCreatedAt(ZonedDateTime.now());
        transaction.setUpdatedAt(ZonedDateTime.now());

        transactionRepository.save(transaction);
        log.info("Transaction created successfully. Transaction ID: {}", transactionId);
    }

    private UUID generateUniqueTransactionId() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (transactionRepository.existsByTransactionId(uuid));
        return uuid;
    }
}
