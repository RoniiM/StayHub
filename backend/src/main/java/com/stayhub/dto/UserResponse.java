package com.stayhub.dto;

import com.stayhub.entity.enums.UserRole;

import java.util.Set;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<UserRole> roles
) {
}
