package com.aiexpense.trackerbackend.service;

import com.aiexpense.trackerbackend.entities.Expense;
import com.aiexpense.trackerbackend.repo.ExpenseRepository;
import com.aiexpense.trackerbackend.service.dto.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("expenseDate", "amount", "createdAt", "id");

  public ExpenseService(ExpenseRepository expenseRepository) {
    this.expenseRepository = expenseRepository;
  }

  /** Build Sort safely from "field,dir" (dir = asc|desc). */
  private Sort buildSort(String sortParam) {
    String[] parts = sortParam != null ? sortParam.split(",", 2) : new String[0];
    String field = parts.length > 0 ? parts[0].trim() : "expenseDate";
    String dir = parts.length > 1 ? parts[1].trim() : "desc";

    if (!ALLOWED_SORT_FIELDS.contains(field))
      field = "expenseDate";
    Sort.Direction direction = "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;

    // stable tie-breaker by id
    return Sort.by(direction, field).and(Sort.by(Sort.Direction.DESC, "id"));
  }

  /** Clamp page index into a safe range based on total elements. */
  private int clampPage(int requestedPage, int size, long total) {
    int totalPages = (int) Math.ceil(total / (double) Math.max(1, size));
    if (totalPages == 0)
      return 0;
    if (requestedPage < 0)
      return 0;
    return Math.min(requestedPage, totalPages - 1);
  }

  /** History (paginated) for a user, using DTO projection. */
  public Page<ExpenseListItemDTO> getUserHistory(Integer userId, int page, int size, String sortParam) {
    Sort sort = buildSort(sortParam);
    long total = expenseRepository.countByUser_IdAndEnabledTrue(userId);
    int safePage = clampPage(page, size, total);
    Pageable pageable = PageRequest.of(safePage, size, sort);
    return expenseRepository.history(userId, pageable);
  }

  public Page<ExpenseListItemDTO> getUserHistory(Integer userId, Pageable pageable) {
    return expenseRepository.history(userId, pageable);
  }

  // -------- CRUD (simple) --------
  @Transactional
  public Expense create(Expense expense) {
    if (expense.getEnabled() == null)
      expense.setEnabled(true);
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

    if (incoming.getUser() != null)
      e.setUser(incoming.getUser());
    if (incoming.getCategory() != null)
      e.setCategory(incoming.getCategory());
    if (incoming.getAmount() != null)
      e.setAmount(incoming.getAmount());
    if (incoming.getDescription() != null)
      e.setDescription(incoming.getDescription());
    if (incoming.getExpenseDate() != null)
      e.setExpenseDate(incoming.getExpenseDate());
    if (incoming.getEnabled() != null)
      e.setEnabled(incoming.getEnabled());

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
    LocalDate end = (endDate != null) ? endDate : today;

    BigDecimal total = expenseRepository.sumAmount(userId, start, end);
    BigDecimal avg = expenseRepository.avgAmount(userId, start, end);

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
            avg == null ? BigDecimal.ZERO : avg),
        topWithPct,
        recent);
  }
}
