package com.retiantonio.device_service.service;

import com.retiantonio.device_service.component.UserClient;
import com.retiantonio.device_service.dto.request.CreateDeviceRequest;
import com.retiantonio.device_service.dto.request.UpdateDeviceRequest;
import com.retiantonio.device_service.dto.response.DeviceResponse;
import com.retiantonio.device_service.exception.DeviceNotFoundException;
import com.retiantonio.device_service.exception.UserNotFoundException;
import com.retiantonio.device_service.model.Device;
import com.retiantonio.device_service.repository.DeviceRepository;
import com.retiantonio.device_service.repository.DeviceSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UserClient userClient;

    // ---- POST section ------------------------------------------------------

    @Transactional
    public DeviceResponse create(CreateDeviceRequest request) {
        UUID ownerUserId = request.ownerUserId();
        if(ownerUserId != null && !userClient.exists(ownerUserId)) {
            throw new UserNotFoundException("User not found: " + ownerUserId + ".");
        }

        Device newDevice = new Device();
        newDevice.setName(request.name());
        newDevice.setDescription(request.description());
        newDevice.setMaxEnergyConsumption(request.maxEnergyConsumption());
        newDevice.setOwnerUserId(request.ownerUserId());

        return toResponse(deviceRepository.save(newDevice));
    }

    // ---- PUT section -------------------------------------------------------

    @Transactional
    public DeviceResponse update(UUID uuid, UpdateDeviceRequest request) {
        Device toUpdateDevice = findOrThrow(uuid);

        if(hasText(request.name())) {
            toUpdateDevice.setName(request.name());
        }

        if(hasText(request.description())) {
            toUpdateDevice.setDescription(request.description());
        }

        if(request.maxEnergyConsumption() != null) {
            toUpdateDevice.setMaxEnergyConsumption(request.maxEnergyConsumption());
        }

        return toResponse(toUpdateDevice);
    }

    // ---- DELETE section ----------------------------------------------------

    @Transactional
    public void delete(UUID uuid) {
        Device toDeleteDevice = findOrThrow(uuid);
        deviceRepository.delete(toDeleteDevice);
    }

    // ---- GET section -------------------------------------------------------

    public  List<DeviceResponse> search(UUID ownerId, String name, Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        UUID effectiveOwner = ownerId;
        if (!admin) {
            UUID self = UUID.fromString(authentication.getName());
            if (ownerId != null && !ownerId.equals(self)) {
                throw new AccessDeniedException("Clients can only view their own devices.");
            }
            effectiveOwner = self;
        }

        return deviceRepository.findAll(DeviceSpecs.filter(effectiveOwner, name))
                .stream().map(this::toResponse).toList();
    }


    // ---- HELPERS section ---------------------------------------------------

    private Device findOrThrow(UUID id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found: " + id + "."));
    }

    private DeviceResponse toResponse(Device d) {
        return new DeviceResponse(d.getId(), d.getName(), d.getDescription(),
                d.getMaxEnergyConsumption(), d.getOwnerUserId());
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
