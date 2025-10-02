package com.aiexpense.trackerbackend.controller;

import com.aiexpense.trackerbackend.service.ExpenseWriteService;
import com.aiexpense.trackerbackend.service.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/create-expenses")
public class ExpenseWriteController {

  private final ExpenseWriteService service;

  public ExpenseWriteController(ExpenseWriteService service) {
    this.service = service;
  }

  @Operation(summary = "Create expense")
  @PostMapping
  public ResponseEntity<ExpenseResponseDTO> create(@Valid @RequestBody ExpenseCreateRequest req) {
    ExpenseResponseDTO created = service.create(req);
    return ResponseEntity.ok(created);// 200 OK
  }

  @Operation(summary = "Update expense (partial)")
  @PatchMapping("/{id}")
  public ExpenseResponseDTO patch(@PathVariable Integer id, @Valid @RequestBody ExpenseUpdateRequest req) {
    return service.update(id, req);
  }

  // If you prefer PUT for full replace, you can reuse ExpenseUpdateRequest
  @Operation(summary = "Update expense (full)")
  @PutMapping("/{id}")
  public ExpenseResponseDTO put(@PathVariable Integer id, @Valid @RequestBody ExpenseUpdateRequest req) {
    return service.update(id, req);
  }
}