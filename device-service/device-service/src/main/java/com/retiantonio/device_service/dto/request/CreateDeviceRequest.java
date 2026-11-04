package com.retiantonio.device_service.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateDeviceRequest(

        @NotBlank(message = "Device name is required.")
        @Size(min = 2, max = 50, message = "Device name must be between 2 and 50 characters.")
        @Schema(example = "Smart Meter")
        String name,

        @Size(max = 150, message = "Device description should be maximum 150 characters.")
        @Schema(example = "Living room energy meter")
        String description,

        @NotNull(message = "A max energy consumption is required.")
        @Positive(message = "Max energy consumption must be greater than 0.")
        @Schema(example = "150.5")
        Double maxEnergyConsumption,

        @Schema(description = "Optional owner", example = "22222222-2222-2222-2222-222222222222")
        UUID ownerUserId
) {}
