package com.retiantonio.authentication_service.dto.request;

import com.retiantonio.authentication_service.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateCredentialRequest(

        @NotBlank(message = "Name is required.")
        @Size(min = 4, max = 20, message = "Name must be between 5 and 20 characters.")
        @Schema(example = "john_doe")
        String username,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters and include an uppercase letter, a lowercase letter, a number and a special character (@$!%*?&)."
        )
        @Schema(example = "Passw0rd123!")
        String password,

        @NotNull(message = "A role must be provided.")
        @Schema(example = "CLIENT")
        Role role,

        @NotNull(message = "No user to link provided.")
        @Schema(example = "22222222-2222-2222-2222-222222222222")
        UUID userId
) {}
