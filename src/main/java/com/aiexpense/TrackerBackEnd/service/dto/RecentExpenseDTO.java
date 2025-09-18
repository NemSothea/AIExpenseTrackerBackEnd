package com.aiexpense.trackerbackend.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RecentExpenseDTO(
  Integer id,
  LocalDate expenseDate,
  String category,
  String description,
  BigDecimal amount
) {}
