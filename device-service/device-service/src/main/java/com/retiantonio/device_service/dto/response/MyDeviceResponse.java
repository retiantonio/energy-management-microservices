package com.retiantonio.device_service.dto.response;

import java.util.UUID;

public record MyDeviceResponse(
        UUID id,
        String name,
        String description,
        double maxEnergyConsumption
) {}