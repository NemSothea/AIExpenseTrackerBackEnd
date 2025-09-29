package com.aiexpense.trackerbackend.controller;

import com.aiexpense.trackerbackend.entities.Expense;
import com.aiexpense.trackerbackend.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

  private final ExpenseService expenseService;

  public ExpenseController(ExpenseService expenseService) {
    this.expenseService = expenseService;
  }

  @GetMapping
  public List<Expense> list() {
    return expenseService.findAll();
  }

  @GetMapping("/{id}")
  public Expense get(@PathVariable Integer id) {
    return expenseService.findOne(id);
  }

  @PostMapping
  public Expense create(@RequestBody Expense expense) {
    return expenseService.create(expense);
  }

  @PutMapping("/{id}")
  public Expense update(@PathVariable Integer id, @RequestBody Expense incoming) {
    return expenseService.update(id, incoming);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Integer id) {
    expenseService.delete(id);
  }

  @PatchMapping("/{id}/disable")
  public Expense disable(@PathVariable Integer id) {
    return expenseService.softDisable(id);
  }
}
