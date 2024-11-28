package com.example.payment.dto;

import com.example.payment.BalticIban;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDTO {

    @Positive(message = "Amount must be a positive number")
    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotBlank(message = "Debtor IBAN is required")
    @BalticIban
    private String debtorIban;
}
