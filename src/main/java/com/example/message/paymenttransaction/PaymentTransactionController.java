package com.example.message.paymenttransaction;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentTransactionController {

    private final PaymentTransactionService service;

    public PaymentTransactionController(PaymentTransactionService service) {
        this.service = service;
    }

    @PostMapping("/api/payments")
    public ResponseEntity<PaymentTransactionResponseDTO> create(@Valid @RequestBody PaymentTransactionRequestDTO request) {
        PaymentTransactionResponseDTO response = service.create(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
