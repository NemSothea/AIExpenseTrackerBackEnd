package com.aiexpense.trackerbackend.controller;

import com.aiexpense.trackerbackend.service.ExpenseService;
import com.aiexpense.trackerbackend.service.dto.DashboardDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

  private final ExpenseService expenseService;

  public DashboardController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  // Example: /api/dashboard?userId=1&start=2025-09-01&end=2025-10-31&topLimit=5&recentLimit=10
  @GetMapping
  public DashboardDTO get(
      @RequestParam Integer userId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
      @RequestParam(defaultValue = "5")  int topLimit,
      @RequestParam(defaultValue = "10") int recentLimit
  ) {
    return expenseService.getDashboard(userId, start, end, topLimit, recentLimit);
  }
}
