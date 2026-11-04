package com.retiantonio.user_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest (

        @Size(min = 4, max = 20, message = "Name must be between 5 and 20 characters")
        @Schema(example = "updated_doe")
        String username,

        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters and include an uppercase letter, a lowercase letter, a number and a special character (@$!%*?&)"
        )
        @Schema(example = "UpdatedPass123!")
        String password,

        @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters")
        @Schema(example = "Updated Doe")
        String name,

        @Email(message = "Email is invalid")
        @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
        @Schema(example = "john@updated.com")
        String email,

        @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
        @Schema(example = "123 Main Updated Street, Cluj-Napoca")
        String address
) {}
