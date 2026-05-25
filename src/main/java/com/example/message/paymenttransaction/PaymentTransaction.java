package com.example.message.paymenttransaction;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "payment_transactions")
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String orderId;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    public PaymentTransaction() {
    }

    public PaymentTransaction(Long id, String orderId, Double amount, Status status) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PaymentTransaction that = (PaymentTransaction) o;
        return Objects.equals(id, that.id) && Objects.equals(orderId, that.orderId) && Objects.equals(amount, that.amount) && status == that.status && Objects.equals(createdAt, that.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId, amount, status, createdAt);
    }

    @Override
    public String toString() {
        return "PaymentTransaction{" +
                "id=" + id +
                ", orderId='" + orderId + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}
