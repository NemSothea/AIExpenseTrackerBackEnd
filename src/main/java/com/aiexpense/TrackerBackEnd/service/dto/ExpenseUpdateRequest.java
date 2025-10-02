package com.aiexpense.trackerbackend.service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseUpdateRequest(
  Integer userId,          // optional
  Integer categoryId,      // optional
  @DecimalMin(value = "0.01") @Digits(integer = 8, fraction = 2) BigDecimal amount,
  String description,
  LocalDate expenseDate,
  Boolean enabled
) {}
