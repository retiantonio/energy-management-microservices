package com.retiantonio.user_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCredentialRequest(

        @Size(min = 4, max = 20, message = "Name must be between 5 and 20 characters")
        @Schema(example = "updated_doe")
        String username,

        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
                message = "Password must be at least 8 characters and include an uppercase letter, a lowercase letter, a number and a special character (@$!%*?&)"
        )
        @Schema(example = "UpdatedPass123!")
        String newPassword

) {}
