package com.retiantonio.device_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateDeviceRequest(

        @Size(min = 2, max = 50, message = "Device name must be between 2 and 50 characters.")
        String name,

        @Size(max = 150, message = "Device description should be maximum 150 characters.")
        String description,

        @Positive(message = "Max energy consumption must be greater than 0.")
        Double maxEnergyConsumption
) {}
