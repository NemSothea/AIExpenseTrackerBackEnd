package com.aiexpense.trackerbackend.service.dto;

import java.math.BigDecimal;

public record TopCategoryDTO(
  Integer categoryId,
  String name,
  BigDecimal totalAmount,
  long txCount,
  Double pctOfTotal // set in service
) {
  public TopCategoryDTO(Integer categoryId, String name, BigDecimal totalAmount, long txCount) {
    this(categoryId, name, totalAmount, txCount, null);
  }
  public TopCategoryDTO withPct(Double pct) {
    return new TopCategoryDTO(categoryId, name, totalAmount, txCount, pct);
  }
}