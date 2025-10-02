package com.aiexpense.trackerbackend.service;

import com.aiexpense.trackerbackend.entities.*;
import com.aiexpense.trackerbackend.service.dto.ExpenseResponseDTO;

public final class ExpenseMapper {
  private ExpenseMapper() {}

  public static ExpenseResponseDTO toResponse(Expense e) {
    return new ExpenseResponseDTO(
      e.getId(),
      e.getUser().getId(),
      e.getCategory().getId(),
      e.getCategory().getName(),
      e.getDescription(),
      e.getExpenseDate(),
      e.getAmount(),
      e.getEnabled()
    );
  }
}
