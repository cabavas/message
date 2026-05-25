package com.example.message.paymenttransaction;

import com.example.message.exception.DuplicateOrderIdException;
import com.example.message.exception.NotFoundException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;

@Service
public class PaymentTransactionService {

    public final PaymentTransactionRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public PaymentTransactionService(PaymentTransactionRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional(readOnly = true)
    public List<PaymentTransactionResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PaymentTransactionResponseDTO findById(Long id) {
        PaymentTransaction transaction = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found with ID: " + id));
        return this.toResponse(transaction);
    }

    @Transactional
    public PaymentTransactionResponseDTO create(PaymentTransactionRequestDTO request) {
        try {
            PaymentTransaction transaction = toEntity(request);
            transaction.setStatus(Status.PENDING);
            transaction.setAmount(request.amount());
            PaymentTransaction saved = repository.save(transaction);

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    rabbitTemplate.convertAndSend("payment.processing", saved);
                }
            });
            return toResponse(saved);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateOrderIdException(request.orderId());
        }
    }

    @Transactional
    public PaymentTransactionResponseDTO update(Long id, PaymentTransactionRequestDTO request) {
        PaymentTransaction existing = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found with ID: " + id));
        existing.setStatus(request.status());
        existing.setAmount(request.amount());
        existing.setOrderId(request.orderId());
        PaymentTransaction updated = repository.save(existing);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        PaymentTransaction transaction = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Transaction not found with ID: " + id));
        repository.delete(transaction);
    }

    private PaymentTransactionResponseDTO toResponse(PaymentTransaction transaction) {
        return new PaymentTransactionResponseDTO(
                transaction.getId(),
                transaction.getOrderId(),
                transaction.getAmount(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }

    private PaymentTransaction toEntity(PaymentTransactionRequestDTO requestDTO) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setAmount(requestDTO.amount());
        transaction.setOrderId(requestDTO.orderId());

        return transaction;
    }
}
