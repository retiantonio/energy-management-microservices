package com.retiantonio.device_service.dto.response;

import java.util.UUID;

public record DeviceResponse(
        UUID id,
        String name,
        String description,
        double maxEnergyConsumption,
        UUID ownerUserId
) {}
