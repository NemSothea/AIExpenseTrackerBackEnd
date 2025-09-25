package com.aiexpense.trackerbackend.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(example = "sothea@example.com") String email,
        @Schema(example = "secret123") String password) {
}