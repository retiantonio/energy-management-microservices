package com.retiantonio.user_service.dto.request;


import com.retiantonio.user_service.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record CreateUserRequest(

        @NotNull(message = "A role must be provided.")
        @Schema(example = "CLIENT")
        Role role,

        @NotBlank(message = "Name is required")
        @Size(min = 4, max = 20, message = "Name must be between 5 and 20 characters")
        @Schema(example = "john_doe")
        String username,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters and include an uppercase letter, a lowercase letter, a number and a special character (@$!%*?&)"
        )
        @Schema(example = "Passw0rd123!")
        String password,

        @NotBlank(message = "Name is required")
        @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters")
        @Schema(example = "John Doe")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        @Schema(example = "john@example.com")
        String email,

        @NotBlank(message = "Address is required")
        @Size(min = 5, max = 255, message = "Address must be at least 5 characters long")
        @Schema(example = "123 Main Street, Cluj-Napoca")
        String address

) {}
