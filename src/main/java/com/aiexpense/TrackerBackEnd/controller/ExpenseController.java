package com.aiexpense.trackerbackend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aiexpense.trackerbackend.model.Expense;
import com.aiexpense.trackerbackend.service.ExpenseService;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    // Get all expenses
    @GetMapping
    public List<Expense> getAllExpenses() {
        return expenseService.getAllExpenses();
    }

    // Get all active expenses
    @GetMapping("/active")
    public List<Expense> getActiveExpenses() {
        return expenseService.getActiveExpenses();
    }

    // Get expense by ID
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable Integer id) {
        Expense expense = expenseService.getExpenseById(id);
        if (expense != null) {
            return ResponseEntity.ok(expense);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create new expense
    @PostMapping
    public Expense createExpense(@RequestBody Expense expense) {
        return expenseService.createExpense(expense);
    }

    // Update expense
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            @PathVariable Integer id,
            @RequestBody Expense expenseDetails) {
        try {
            Expense updatedExpense = expenseService.updateExpense(id, expenseDetails);
            return ResponseEntity.ok(updatedExpense);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete expense permanently
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(@PathVariable Integer id) {
        try {
            expenseService.deleteExpense(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Get expenses by user ID
    @GetMapping("/user/{userId}")
    public List<Expense> getExpensesByUser(@PathVariable Integer userId) {
        return expenseService.getExpensesByUserId(userId);
    }

    // Get expenses by category ID
    @GetMapping("/category/{categoryId}")
    public List<Expense> getExpensesByCategory(@PathVariable Integer categoryId) {
        return expenseService.getExpensesByCategoryId(categoryId);
    }

    // Get expenses by date range
    @GetMapping("/date-range")
    public List<Expense> getExpensesByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return expenseService.getExpensesByDateRange(startDate, endDate);
    }

    // Get total expenses by user
    @GetMapping("/user/{userId}/total")
    public BigDecimal getTotalExpensesByUser(@PathVariable Integer userId) {
        return expenseService.getTotalExpensesByUser(userId);
    }

    // Get total expenses by category
    @GetMapping("/category/{categoryId}/total")
    public BigDecimal getTotalExpensesByCategory(@PathVariable Integer categoryId) {
        return expenseService.getTotalExpensesByCategory(categoryId);
    }
}
