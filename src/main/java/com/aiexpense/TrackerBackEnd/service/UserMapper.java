package com.aiexpense.trackerbackend.service;

import com.aiexpense.trackerbackend.entities.Users;
import com.aiexpense.trackerbackend.service.dto.UserDTO;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserDTO toDTO(Users u) {
        if (u == null)
            return null;
        return new UserDTO(u.getId(), u.getName(), u.getEmail(), u.getRole(), u.getContact(), u.isEnabled());
    }
}