package com.julio.Financial.management.domain.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.julio.Financial.management.domain.enumerated.TransactionType;
import com.julio.Financial.management.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "tb_transactions")
public class Transaction {

    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    private UUID id;

    @Column(nullable = false)
    private TransactionType type;

    @Size(min = 2, max = 30)
    private String payment;

    @Column(nullable = false)
    @Positive
    private BigDecimal amount;

    @Size(min = 3, max = 150)
    private String description;

    @Column(nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate timestamp;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Transaction(){
    }

    public Transaction(TransactionType type, String payment, BigDecimal amount, String description, LocalDate timestamp, User user) {
        this.type = type;
        this.payment = payment;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public @Size(min = 3, max = 150) String getDescription() {
        return description;
    }

    public void setDescription(@Size(min = 3, max = 150) String description) {
        this.description = description;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
