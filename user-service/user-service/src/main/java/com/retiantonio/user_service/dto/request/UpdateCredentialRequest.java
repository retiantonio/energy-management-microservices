package com.retiantonio.user_service.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCredentialRequest(

        @Size(min = 4, max = 20, message = "Name must be between 5 and 20 characters")
        String username,

        @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
        // TODO: also add a regex here for password strength
        String newPassword

) {}
