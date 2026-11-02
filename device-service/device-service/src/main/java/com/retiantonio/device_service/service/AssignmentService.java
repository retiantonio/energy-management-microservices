package com.retiantonio.device_service.service;

import com.retiantonio.device_service.component.UserClient;
import com.retiantonio.device_service.dto.request.AssignDeviceRequest;
import com.retiantonio.device_service.dto.response.DeviceResponse;
import com.retiantonio.device_service.exception.DeviceNotFoundException;
import com.retiantonio.device_service.exception.UserNotFoundException;
import com.retiantonio.device_service.model.Device;
import com.retiantonio.device_service.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final DeviceRepository deviceRepository;
    private final UserClient userClient;

    // ---- PUT section -------------------------------------------------------

    @Transactional
    public DeviceResponse assign(UUID deviceId, AssignDeviceRequest request) {
        Device device = findOrThrow(deviceId);
        UUID userId = request.userId();
        if(!userClient.exists(userId)) {
            throw new UserNotFoundException("User not found: " + userId);
        }

        device.setOwnerUserId(userId);
        return toResponse(device);
    }

    // ---- DELETE section ----------------------------------------------------

    @Transactional
    public DeviceResponse unassign(UUID deviceId) {
        Device device = findOrThrow(deviceId);
        device.setOwnerUserId(null);
        return toResponse(device);
    }

    @Transactional
    public void unassignAllFrom(UUID userId) {
        deviceRepository.unassignAllFrom(userId);
    }

    // ---- GET section -------------------------------------------------------

    @Transactional(readOnly = true)
    public List<DeviceResponse> getByOwner(UUID userId) {
        return deviceRepository.findByOwnerUserId(userId).stream()
                .map(this::toResponse).toList();
    }

    // ---- HELPERS section ---------------------------------------------------

    private Device findOrThrow(UUID id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + id));
    }

    private DeviceResponse toResponse(Device d) {
        return new DeviceResponse(d.getId(), d.getName(), d.getDescription(),
                d.getMaxEnergyConsumption(), d.getOwnerUserId());
    }
}
