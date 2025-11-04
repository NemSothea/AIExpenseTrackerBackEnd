package com.aiexpense.trackerbackend.service;

import com.aiexpense.trackerbackend.entities.*;

import com.aiexpense.trackerbackend.repo.CategoryRepository;
import com.aiexpense.trackerbackend.repo.ExpenseRepository;
import com.aiexpense.trackerbackend.repo.UserRepository;
import com.aiexpense.trackerbackend.service.dto.ExpenseCreateRequest;
import com.aiexpense.trackerbackend.service.dto.ExpenseUpdateRequest;
import com.aiexpense.trackerbackend.service.dto.ExpenseResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseWriteService {

  private final ExpenseRepository expenseRepo;
  private final UserRepository userRepo;
  private final CategoryRepository categoryRepo;

  public ExpenseWriteService(ExpenseRepository expenseRepo, UserRepository userRepo, CategoryRepository categoryRepo) {
    this.expenseRepo = expenseRepo;
    this.userRepo = userRepo;
    this.categoryRepo = categoryRepo;
  }

  @Transactional
  public ExpenseResponseDTO create(ExpenseCreateRequest req) {
    Users user = userRepo.findById(req.userId())
        .orElseThrow(() -> new EntityNotFoundException("User not found: " + req.userId()));
    Category cat = categoryRepo.findById(req.categoryId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found: " + req.categoryId()));

    Expense e = Expense.builder()
        .user(user)
        .category(cat)
        .amount(req.amount())
        .description(req.description())
        .expenseDate(req.expenseDate())
        .enabled(true)
        .build();

    e = expenseRepo.save(e);
    return ExpenseMapper.toResponse(e);
  }

  @Transactional
  public ExpenseResponseDTO update(Integer id, ExpenseUpdateRequest req) {
    Expense e = expenseRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Expense not found: " + id));

    if (req.userId() != null) {
      Users user = userRepo.findById(req.userId())
          .orElseThrow(() -> new EntityNotFoundException("User not found: " + req.userId()));
      e.setUser(user);
    }
    if (req.categoryId() != null) {
      Category cat = categoryRepo.findById(req.categoryId())
          .orElseThrow(() -> new EntityNotFoundException("Category not found: " + req.categoryId()));
      e.setCategory(cat);
    }
    if (req.amount() != null)
      e.setAmount(req.amount());
    if (req.description() != null)
      e.setDescription(req.description());
    if (req.expenseDate() != null)
      e.setExpenseDate(req.expenseDate());
    if (req.enabled() != null)
      e.setEnabled(req.enabled());

    e = expenseRepo.save(e);
    return ExpenseMapper.toResponse(e);
  }

  @Transactional
  public void delete(Integer id) {
    Expense expense = expenseRepo.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Expense not found: " + id));
    expense.setEnabled(false); // Soft delete
    expenseRepo.save(expense);
  }
}