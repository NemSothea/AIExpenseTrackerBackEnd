package com.aiexpense.trackerbackend.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aiexpense.trackerbackend.model.Expense;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    // Find all active expenses
    List<Expense> findByIsactiveTrue();
    
    // Find expenses by user ID
    List<Expense> findByUserIdAndIsactiveTrue(Integer userId);
    
    // Find expenses by category ID
    List<Expense> findByCategoryIdAndIsactiveTrue(Integer categoryId);
    
    // Find expenses by user and category
    List<Expense> findByUserIdAndCategoryIdAndIsactiveTrue(Integer userId, Integer categoryId);
    
    // Find expenses within date range
    List<Expense> findByExpenseDateBetweenAndIsactiveTrue(LocalDate startDate, LocalDate endDate);
    
    // Find expenses by user within date range
    List<Expense> findByUserIdAndExpenseDateBetweenAndIsactiveTrue(Integer userId, LocalDate startDate, LocalDate endDate);
}
