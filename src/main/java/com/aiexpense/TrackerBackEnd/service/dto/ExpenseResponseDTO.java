package com.aiexpense.trackerbackend.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseResponseDTO(
  Integer id,
  Integer userId,
  Integer categoryId,
  String  category,
  String  description,
  LocalDate expenseDate,
  BigDecimal amount,
  Boolean enabled
) {}
