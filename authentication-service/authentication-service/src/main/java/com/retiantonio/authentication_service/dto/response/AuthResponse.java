package com.retiantonio.authentication_service.dto.response;

import com.retiantonio.authentication_service.model.Role;

import java.util.UUID;

public record AuthResponse(
        String token,
        String username,
        UUID userId,
        Role role,
        long expiresInSeconds
) {}
