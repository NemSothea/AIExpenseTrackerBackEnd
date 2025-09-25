package com.aiexpense.trackerbackend.service.dto;


public record AuthResponse(
        String token,
        String email
) {
}
