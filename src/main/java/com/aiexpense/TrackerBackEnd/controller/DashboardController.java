package com.aiexpense.trackerbackend.controller;


import com.aiexpense.trackerbackend.service.ExpenseService;
import com.aiexpense.trackerbackend.service.dto.DashboardDTO;
import com.aiexpense.trackerbackend.service.dto.ExpenseListItemDTO;


import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.Set;


@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final ExpenseService expenseService;

  public DashboardController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  /**
   * Tab 1: summary dashboard (totals, top categories, recent)
   * Example: /api/dashboard?userId=1
   */
  // Example:
  // /api/dashboard?userId=1&start=2025-09-01&end=2025-10-31&topLimit=5&recentLimit=10
  @GetMapping
  public DashboardDTO get(
      @RequestParam Integer userId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
      @RequestParam(defaultValue = "5") int topLimit,
      @RequestParam(defaultValue = "10") int recentLimit) {
    return expenseService.getDashboard(userId, start, end, topLimit, recentLimit);
  }


  /**
   * Tab 2: full expense history for user with pagination
   * Example: /api/dashboard/history?userId=1&start=2025-09-01&end=2025-10-31
   */
  @GetMapping("/history-pagination")
public Page<ExpenseListItemDTO> getHistory(
    @RequestParam Integer userId,
    @RequestParam(defaultValue = "0")  int page,
    @RequestParam(defaultValue = "20") int size,
    @RequestParam(defaultValue = "expenseDate,desc") String sort
) {
    return expenseService.getUserHistory(userId, page, size, sort);
}

}
