package com.aiexpense.trackerbackend.service;

import com.aiexpense.trackerbackend.model.Expense;
import com.aiexpense.trackerbackend.repo.ExpenseRepository;
import com.aiexpense.trackerbackend.service.dto.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ExpenseService {

  private final ExpenseRepository expenseRepository;

  public ExpenseService(ExpenseRepository expenseRepository) {
    this.expenseRepository = expenseRepository;
  }

  // -------- CRUD (simple) --------
  @Transactional
  public Expense create(Expense expense) {
    if (expense.getEnabled() == null) expense.setEnabled(true);
    return expenseRepository.save(expense);
  }

  public Expense findOne(Integer id) {
    return expenseRepository.findById(id).orElse(null);
  }

  public List<Expense> findAll() {
    return expenseRepository.findAll();
  }

  @Transactional
  public Expense update(Integer id, Expense incoming) {
    Expense e = expenseRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Expense not found: " + id));

    if (incoming.getUser() != null) e.setUser(incoming.getUser());
    if (incoming.getCategory() != null) e.setCategory(incoming.getCategory());
    if (incoming.getAmount() != null) e.setAmount(incoming.getAmount());
    if (incoming.getDescription() != null) e.setDescription(incoming.getDescription());
    if (incoming.getExpenseDate() != null) e.setExpenseDate(incoming.getExpenseDate());
    if (incoming.getEnabled() != null) e.setEnabled(incoming.getEnabled());

    return expenseRepository.save(e);
  }

  @Transactional
  public void delete(Integer id) {
    expenseRepository.deleteById(id);
  }

  @Transactional
  public Expense softDisable(Integer id) {
    Expense e = expenseRepository.findById(id)
      .orElseThrow(() -> new IllegalArgumentException("Expense not found: " + id));
    e.setEnabled(false);
    return expenseRepository.save(e);
  }

  // -------- Dashboard summary --------
  public DashboardDTO getDashboard(Integer userId,
                                   LocalDate startDate,
                                   LocalDate endDate,
                                   int topLimit,
                                   int recentLimit) {

    LocalDate today = LocalDate.now();
    LocalDate start = (startDate != null) ? startDate : today.withDayOfMonth(1);
    LocalDate end   = (endDate   != null) ? endDate   : today;

    BigDecimal total = expenseRepository.sumAmount(userId, start, end);
    BigDecimal avg   = expenseRepository.avgAmount(userId, start, end);

    List<TopCategoryDTO> top = expenseRepository.topCategories(
        userId, start, end, PageRequest.of(0, Math.max(1, topLimit)));

    double grand = total == null ? 0d : total.doubleValue();
    List<TopCategoryDTO> topWithPct = top.stream()
        .map(t -> {
          double pct = (grand == 0d) ? 0d : (t.totalAmount().doubleValue() * 100.0 / grand);
          return t.withPct(Math.round(pct * 100.0) / 100.0);
        })
        .toList();

    List<RecentExpenseDTO> recent = expenseRepository.recentByUser(
        userId, PageRequest.of(0, Math.max(1, recentLimit)));

    return new DashboardDTO(
        new DashboardDTO.Totals(
            total == null ? BigDecimal.ZERO : total,
            avg   == null ? BigDecimal.ZERO : avg
        ),
        topWithPct,
        recent
    );
  }
}
