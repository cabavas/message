package com.example.message.exception;

public class DuplicateOrderIdException extends RuntimeException {
    public DuplicateOrderIdException(String orderId) {
        super("Order ID '" + orderId + "' already existis");
    }
}
