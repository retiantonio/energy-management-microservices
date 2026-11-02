package com.retiantonio.authentication_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Username is required.")
        @Size(min = 4, max = 20, message = "Username must be between 4 and 20 characters")
        String username,

        @NotBlank(message = "Password is required.")
        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
        // TODO: also add a regex here for password strength
        String password,

        @NotBlank(message = "Name is required.")
        @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters.")
        String name,

        @NotBlank(message = "Email is required.")
        @Email(message = "Email is invalid.")
        String email,

        @NotBlank(message = "Address is required.")
        @Size(min = 5, max = 255, message = "Address must be at least 5 characters long.")
        String address
) {}
