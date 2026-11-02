package com.retiantonio.authentication_service.dto.response;

import com.retiantonio.authentication_service.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CredentialResponse(
    UUID uuid,
    String username,
    Role role,
    UUID userId
) {}
