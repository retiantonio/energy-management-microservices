package com.retiantonio.device_service.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignDeviceRequest(
        @NotNull(message = "An user it's needed for an assignment.")
        UUID userId
) {}
