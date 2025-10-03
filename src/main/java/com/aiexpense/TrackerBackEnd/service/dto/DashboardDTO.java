package com.aiexpense.trackerbackend.service.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardDTO(
  Totals totals,
  List<TopCategoryDTO> top_categories,
  List<RecentExpenseDTO> recent_expenses
) {
  public static record Totals(BigDecimal total_expenses, BigDecimal average_expense) {}
}
