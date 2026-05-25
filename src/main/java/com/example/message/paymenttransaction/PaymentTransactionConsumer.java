package com.example.message.paymenttransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class PaymentTransactionConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentTransactionConsumer.class);
    private final PaymentTransactionRepository repository;
    private final RestClient restClient;

    public PaymentTransactionConsumer(PaymentTransactionRepository repository) {
        this.repository = repository;
        this.restClient = RestClient.create("https://httpbin.org");
    }

    @RabbitListener(queues = "payment.processing")
    public void processPayment(PaymentTransaction transaction) {
        log.info("Transaction received for processing: {}", transaction.getOrderId());
        try {
            String mockResponse = restClient.post()
                    .uri("/post")
                    .body(transaction)
                    .retrieve()
                    .body(String.class);
            log.info("Mock API response: {}", mockResponse);

            Status newStatus;
            if(transaction.getAmount() > 1000.00) {
                newStatus = Status.REJECTED;
            } else {
                newStatus = Status.APPROVED;
            }

            transaction.setStatus(newStatus);
            repository.save(transaction);
            log.info("Transaction {} updated with status: {}", transaction.getOrderId(), newStatus);
        } catch (Exception e) {
            log.error("Error while processing transaction {}: {}", transaction.getOrderId(), e.getMessage());
        }
    }
}
