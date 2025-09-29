package com.aiexpense.trackerbackend.repo;

import com.aiexpense.trackerbackend.entities.Expense;
import com.aiexpense.trackerbackend.service.dto.ExpenseListItemDTO;
import com.aiexpense.trackerbackend.service.dto.RecentExpenseDTO;
import com.aiexpense.trackerbackend.service.dto.TopCategoryDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
  
  long countByUser_IdAndEnabledTrue(Integer userId);

  @Query("""
      select new com.aiexpense.trackerbackend.service.dto.ExpenseListItemDTO(
        e.id, e.category.name, e.description, e.expenseDate, e.amount
      )
      from Expense e
      where e.enabled = true and e.user.id = :userId
    """)
    Page<ExpenseListItemDTO> history(@Param("userId") Integer userId, Pageable pageable);

  // Totals
  @Query("""
        select coalesce(sum(e.amount), 0)
        from Expense e
        where e.enabled = true
          and e.user.id = :userId
          and e.expenseDate between :start and :end
      """)
  BigDecimal sumAmount(@Param("userId") Integer userId,
      @Param("start") LocalDate start,
      @Param("end") LocalDate end);

  @Query("""
        select coalesce(avg(e.amount), 0)
        from Expense e
        where e.enabled = true
          and e.user.id = :userId
          and e.expenseDate between :start and :end
      """)
  BigDecimal avgAmount(@Param("userId") Integer userId,
      @Param("start") LocalDate start,
      @Param("end") LocalDate end);

  // Top categories
  @Query("""
        select new com.aiexpense.trackerbackend.service.dto.TopCategoryDTO(
          e.category.id, e.category.name, sum(e.amount), count(e)
        )
        from Expense e
        where e.enabled = true
          and e.user.id = :userId
          and e.expenseDate between :start and :end
        group by e.category.id, e.category.name
        order by sum(e.amount) desc
      """)
  List<TopCategoryDTO> topCategories(@Param("userId") Integer userId,
      @Param("start") LocalDate start,
      @Param("end") LocalDate end,
      Pageable pageable);

  // Recent expenses
  @Query("""
        select new com.aiexpense.trackerbackend.service.dto.RecentExpenseDTO(
          e.id, e.expenseDate, e.category.name, e.description, e.amount
        )
        from Expense e
        where e.enabled = true
          and e.user.id = :userId
        order by e.expenseDate desc, e.id desc
      """)
  List<RecentExpenseDTO> recentByUser(@Param("userId") Integer userId, Pageable pageable);
}
