package com.retiantonio.user_service.dto.response;

import com.retiantonio.user_service.model.Role;

import java.util.UUID;

public record UserResponse(
        UUID uuid,
        String name,
        String email,
        String address,
        Role role
) {}
