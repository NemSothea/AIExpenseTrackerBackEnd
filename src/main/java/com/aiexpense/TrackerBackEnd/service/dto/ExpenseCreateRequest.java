package com.aiexpense.trackerbackend.service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseCreateRequest(
  @NotNull Integer userId,
  @NotNull Integer categoryId,
  @NotNull @DecimalMin(value = "0.01") @Digits(integer = 8, fraction = 2) BigDecimal amount,
  @NotBlank String description,
  @NotNull LocalDate expenseDate
) {}
