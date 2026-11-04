package com.retiantonio.authentication_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Username is required.")
        @Schema(example = "admin")
        String username,

        @NotBlank(message = "Password is required.")
        @Schema(example = "your-password")
        String password
) {}
