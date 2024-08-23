package com.julio.Financial.management.DTO;

import java.math.BigDecimal;

public record SummaryDTO(BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal balance, String status) {
    public SummaryDTO(BigDecimal totalIncome, BigDecimal totalExpense){
        this(totalIncome, totalExpense, totalIncome.subtract(totalExpense),
                totalIncome.subtract(totalExpense).signum() >= 0 ? "Positive" : "Negative");
    }

}