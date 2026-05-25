package com.example.message.paymenttransaction;

import java.time.LocalDateTime;

public record PaymentTransactionResponseDTO(
        Long id,
        String orderId,
        Double amount,
        Status status,
        LocalDateTime createdAt
) {
}
