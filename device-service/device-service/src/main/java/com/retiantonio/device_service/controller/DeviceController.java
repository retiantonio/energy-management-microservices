package com.retiantonio.device_service.controller;

import com.retiantonio.device_service.dto.request.AssignDeviceRequest;
import com.retiantonio.device_service.dto.request.CreateDeviceRequest;
import com.retiantonio.device_service.dto.request.UpdateDeviceRequest;
import com.retiantonio.device_service.dto.response.DeviceResponse;
import com.retiantonio.device_service.model.Device;
import com.retiantonio.device_service.service.AssignmentService;
import com.retiantonio.device_service.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Devices", description = "Device management and assignment")
public class DeviceController {

    private final AssignmentService assignmentService;
    private final DeviceService deviceService;

    // ---- POST section ------------------------------------------------------

    @Operation(summary = "Create a device", description = "Optionally assigns it to an existing user. ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "404", description = "Owner not found")})

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

    @Operation(summary = "Update a device", description = "Partial or full update. ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "404", description = "Device not found")})

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceResponse> update(@Parameter(description = "Device UUID") @PathVariable UUID uuid,
                                               @Valid @RequestBody UpdateDeviceRequest request) {
        return ResponseEntity.ok(deviceService.update(uuid, request));
    }

    @Operation(summary = "Assign a device to a user", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Assigned"),
            @ApiResponse(responseCode = "400", description = "Missing userId"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "404", description = "Device or user not found")})

    @PutMapping("/{uuid}/assignment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceResponse> assign(@Parameter(description = "Device UUID") @PathVariable UUID uuid,
                                                 @Valid @RequestBody AssignDeviceRequest request) {
        return ResponseEntity.ok(assignmentService.assign(uuid, request));
    }

    // ---- DELETE section ----------------------------------------------------

    @Operation(summary = "Delete a device", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "404", description = "Device not found")})

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "Device UUID") @PathVariable UUID uuid) {
        deviceService.delete(uuid);
    }

    @Operation(summary = "Unassign a device from its owner", description = "ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Unassigned"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "404", description = "Device not found")})

    @DeleteMapping("/{uuid}/assignment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DeviceResponse> unassign(@Parameter(description = "Device UUID") @PathVariable UUID uuid) {
        return ResponseEntity.ok(assignmentService.unassign(uuid));
    }

    @Operation(summary = "Unassign all devices of a user", description = "Used by user-service on user deletion. ADMIN or INTERNAL.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "All devices unassigned"),
            @ApiResponse(responseCode = "403", description = "Not allowed")})

    @DeleteMapping("/user/{userId}/assignments")
    @PreAuthorize("hasAnyRole('ADMIN', 'INTERNAL')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unassignAll(@Parameter(description = "User UUID") @PathVariable UUID userId) {
        assignmentService.unassignAllFrom(userId);
    }

    // ---- GET section -------------------------------------------------------

    @Operation(summary = "Search devices", description = "ADMIN sees all; CLIENT only their own.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of matching devices"),
            @ApiResponse(responseCode = "403", description = "Client asked for another owner's devices")})

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT')")
    public ResponseEntity<List<DeviceResponse>> getDevices(
            @Parameter(description = "Filter by owner UUID") @RequestParam(required = false) UUID ownerId,
            @Parameter(description = "Filter by device name") @RequestParam(required = false) String name,
            @Parameter(hidden = true) Authentication auth) {

        return ResponseEntity.ok(deviceService.search(ownerId, name, auth));
    }
}
