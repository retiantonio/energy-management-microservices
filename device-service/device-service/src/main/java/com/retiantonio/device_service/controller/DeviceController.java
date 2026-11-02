package com.retiantonio.device_service.controller;

import com.retiantonio.device_service.dto.request.AssignDeviceRequest;
import com.retiantonio.device_service.dto.request.CreateDeviceRequest;
import com.retiantonio.device_service.dto.request.UpdateDeviceRequest;
import com.retiantonio.device_service.dto.response.DeviceResponse;
import com.retiantonio.device_service.model.Device;
import com.retiantonio.device_service.service.AssignmentService;
import com.retiantonio.device_service.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final AssignmentService assignmentService;
    private final DeviceService deviceService;

    // ---- POST section ------------------------------------------------------

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceResponse> create(@Valid @RequestBody CreateDeviceRequest request) {
        DeviceResponse created = deviceService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // ---- PUT section -------------------------------------------------------

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceResponse> update(@PathVariable UUID uuid,
                                               @Valid @RequestBody UpdateDeviceRequest request) {
        return ResponseEntity.ok(deviceService.update(uuid, request));
    }


    @PutMapping("/{uuid}/assignment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceResponse> assign(@PathVariable UUID uuid,
                                                 @Valid @RequestBody AssignDeviceRequest request) {
        return ResponseEntity.ok(assignmentService.assign(uuid, request));
    }

    // ---- DELETE section ----------------------------------------------------

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        deviceService.delete(uuid);
    }

    @DeleteMapping("/{uuid}/assignment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceResponse> unassign(@PathVariable UUID uuid) {
        return ResponseEntity.ok(assignmentService.unassign(uuid));
    }

    @DeleteMapping("/user/{userId}/assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'INTERNAL')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignAll(@PathVariable UUID userId) {
        assignmentService.unassignAllFrom(userId);
    }

    // ---- GET section -------------------------------------------------------

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<List<DeviceResponse>> getDevices(
            @RequestParam(required = false) UUID ownerId,
            @RequestParam(required = false) String name,
            Authentication auth) {

        return ResponseEntity.ok(deviceService.search(ownerId, name, auth));
    }
}
