package com.aiexpense.trackerbackend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Data @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name = "expenses")
public class Expense {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne(optional = false) @JoinColumn(name = "user_id", nullable = false)
  private Users user;

  @ManyToOne(optional = false) @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @NotNull @Digits(integer = 8, fraction = 2)
  @Column(nullable = false, precision = 10, scale = 2)
  private BigDecimal amount;

  @NotBlank @Column(nullable = false)
  private String description;

  @NotNull @Column(name = "expense_date", nullable = false)
  private LocalDate expenseDate;

  @Builder.Default
  private Boolean enabled = true;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;
}
