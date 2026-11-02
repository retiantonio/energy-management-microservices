package com.retiantonio.user_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest (

        @Size(min = 4, max = 20, message = "Name must be between 5 and 20 characters")
        String username,

        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
        // TODO: also add a regex here for password strength
        String password,

        @Size(min = 5, max = 20, message = "Name must be between 5 and 20 characters")
        String name,

        @Email(message = "Email is invalid")
        @Size(min = 5, max = 100, message = "Email must be between 5 and 100 characters")
        String email,

        @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
        String address
) {}
