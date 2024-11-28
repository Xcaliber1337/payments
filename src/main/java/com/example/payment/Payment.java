package com.example.payment;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    private UUID id;

    @Positive
    @NotNull
    private BigDecimal amount;

    @NotNull
    @BalticIban
    private String debtorIban;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private String clientCountry;
}
