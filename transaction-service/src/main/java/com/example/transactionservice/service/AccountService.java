package com.example.transactionservice.service;

import com.example.dao.model.Account;
import com.example.dao.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Account getByAccountNumber(final String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
    }

    public void updateBalances(final Account sender, final Account receiver, final BigDecimal amount) {
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        final ZonedDateTime now = ZonedDateTime.now();
        sender.setUpdatedAt(now);
        receiver.setUpdatedAt(now);

        accountRepository.save(sender);
        accountRepository.save(receiver);
    }
}
