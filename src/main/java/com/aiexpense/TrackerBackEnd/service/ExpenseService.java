package com.aiexpense.trackerbackend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aiexpense.trackerbackend.model.Expense;
import com.aiexpense.trackerbackend.repo.ExpenseRepository;

@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    // Get all expenses
    public List<Expense> getAllExpenses() {
        return (List<Expense>) expenseRepository.findAll();
    }
    
    // Get all active expenses
    public List<Expense> getActiveExpenses() {
        return expenseRepository.findByIsactiveTrue();
    }
    
    // Get expense by ID
    public Expense getExpenseById(Integer id) {
        return expenseRepository.findById(id).orElse(null);
    }
    
    // Create new expense
    public Expense createExpense(Expense expense) {
        if (expense.getIsactive() == null) {
            expense.setIsactive(true);
        }
        return expenseRepository.save(expense);
    }
    
    // Update existing expense
    public Expense updateExpense(Integer id, Expense expenseDetails) {
        Expense existingExpense = getExpenseById(id);
        if (existingExpense == null) {
            throw new RuntimeException("Expense not found with id: " + id);
        }
        
        if (expenseDetails.getUser() != null) {
            existingExpense.setUser(expenseDetails.getUser());
        }
        if (expenseDetails.getCategory() != null) {
            existingExpense.setCategory(expenseDetails.getCategory());
        }
        if (expenseDetails.getAmount() != null) {
            existingExpense.setAmount(expenseDetails.getAmount());
        }
        if (expenseDetails.getDescription() != null) {
            existingExpense.setDescription(expenseDetails.getDescription());
        }
        if (expenseDetails.getExpenseDate() != null) {
            existingExpense.setExpenseDate(expenseDetails.getExpenseDate());
        }
        if (expenseDetails.getIsactive() != null) {
            existingExpense.setIsactive(expenseDetails.getIsactive());
        }
        
        return expenseRepository.save(existingExpense);
    }
    
    // Delete expense permanently
    public void deleteExpense(Integer id) {
        expenseRepository.deleteById(id);
    }
    
    // Soft delete expense
    public void softDeleteExpense(Integer id) {
        Expense expense = getExpenseById(id);
        if (expense != null) {
            expense.setIsactive(false);
            expenseRepository.save(expense);
        }
    }
    
    // Get expenses by user ID
    public List<Expense> getExpensesByUserId(Integer userId) {
        return expenseRepository.findByUserIdAndIsactiveTrue(userId);
    }
    
    // Get expenses by category ID
    public List<Expense> getExpensesByCategoryId(Integer categoryId) {
        return expenseRepository.findByCategoryIdAndIsactiveTrue(categoryId);
    }
    
    // Get expenses within date range
    public List<Expense> getExpensesByDateRange(LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByExpenseDateBetweenAndIsactiveTrue(startDate, endDate);
    }
    
    // Get expenses by user and date range
    public List<Expense> getExpensesByUserAndDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        return expenseRepository.findByUserIdAndExpenseDateBetweenAndIsactiveTrue(userId, startDate, endDate);
    }
    
    // Get total expenses amount by user
    public BigDecimal getTotalExpensesByUser(Integer userId) {
        List<Expense> expenses = getExpensesByUserId(userId);
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Get total expenses amount by category
    public BigDecimal getTotalExpensesByCategory(Integer categoryId) {
        List<Expense> expenses = getExpensesByCategoryId(categoryId);
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
