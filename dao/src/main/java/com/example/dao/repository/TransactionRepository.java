package com.example.dao.repository;

import com.example.dao.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsByTransactionId(final UUID transactionId);
}
