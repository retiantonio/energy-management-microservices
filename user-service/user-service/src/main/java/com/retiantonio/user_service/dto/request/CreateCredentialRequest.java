package com.retiantonio.user_service.dto.request;

import com.retiantonio.user_service.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCredentialRequest(

        @NotBlank(message = "Name is required")
        @Size(min = 4, max = 20, message = "Name must be between 5 and 20 characters")
        String username,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
        // TODO: also add a regex here for password strength
        String password,

        @NotNull(message = "A role must be provided.")
        Role role,

        @NotNull(message = "No user to link provided.")
        UUID userId
) {}
