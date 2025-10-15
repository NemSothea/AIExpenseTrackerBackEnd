package com.aiexpense.trackerbackend.service.dto;

public record UserDTO(
    Integer id,
    String name,
    String email,
    String role,
    String contact,
    boolean enabled
) {}
