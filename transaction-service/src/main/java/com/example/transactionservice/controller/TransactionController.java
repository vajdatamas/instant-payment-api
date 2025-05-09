package com.example.transactionservice.controller;

import com.example.transactionservice.resource.request.TransactionRequest;
import com.example.transactionservice.resource.response.TransactionResponse;
import com.example.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Validated
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody final TransactionRequest transactionRequest) {
        final TransactionResponse response = transactionService.createTransaction(transactionRequest);
        return ResponseEntity.ok(response);
    }
}
