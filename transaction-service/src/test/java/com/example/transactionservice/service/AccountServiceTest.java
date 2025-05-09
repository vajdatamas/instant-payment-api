package com.example.transactionservice.service;

import com.example.dao.model.Account;
import com.example.dao.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void getByAccountNumber_shouldReturnAccount_whenExists() {
        final var account = new Account();
        account.setAccountNumber("123");

        when(accountRepository.findByAccountNumber("123")).thenReturn(Optional.of(account));

        final var result = accountService.getByAccountNumber("123");

        assertThat(result).isEqualTo(account);
    }

    @Test
    void getByAccountNumber_shouldThrow_whenNotFound() {
        when(accountRepository.findByAccountNumber("123")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getByAccountNumber("123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account not found");
    }

    @Test
    void updateBalances_shouldUpdateAndSaveAccounts() {
        final var sender = new Account();
        sender.setBalance(BigDecimal.valueOf(500));

        final var receiver = new Account();
        receiver.setBalance(BigDecimal.valueOf(200));

        final var amount = BigDecimal.valueOf(100);

        accountService.updateBalances(sender, receiver, amount);

        assertThat(sender.getBalance()).isEqualTo(BigDecimal.valueOf(400));
        assertThat(receiver.getBalance()).isEqualTo(BigDecimal.valueOf(300));

        verify(accountRepository).save(sender);
        verify(accountRepository).save(receiver);
    }
}
