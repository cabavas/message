package com.example.message.paymenttransaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record PaymentTransactionRequestDTO(
        @NotBlank
        String orderId,
        @Positive
        Double amount,
        Status status
) {
}
