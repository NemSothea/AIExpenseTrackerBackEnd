package com.aiexpense.trackerbackend.service.dto;

public record ExpenseListItemDTO(
    Integer id,
    String category,        // category name
    String description,
    java.time.LocalDate expenseDate,
    java.math.BigDecimal amount
) {}
