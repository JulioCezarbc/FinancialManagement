package com.julio.Financial.management.DTO;

import com.julio.Financial.management.domain.enumerated.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionDTO(TransactionType type, String payment, BigDecimal amount, String description, LocalDate timestamp, String email) {
}
