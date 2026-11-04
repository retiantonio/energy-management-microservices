package com.retiantonio.device_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignDeviceRequest(
        @NotNull(message = "An user it's needed for an assignment.")
        @Schema(example = "22222222-2222-2222-2222-222222222222")
        UUID userId
) {}
