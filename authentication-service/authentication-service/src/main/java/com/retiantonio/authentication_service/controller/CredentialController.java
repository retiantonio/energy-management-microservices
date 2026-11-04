package com.retiantonio.authentication_service.controller;

import com.retiantonio.authentication_service.dto.request.CreateCredentialRequest;
import com.retiantonio.authentication_service.dto.request.UpdateCredentialsRequest;
import com.retiantonio.authentication_service.dto.response.CredentialResponse;
import com.retiantonio.authentication_service.model.Credential;
import com.retiantonio.authentication_service.service.CredentialService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.CacheResponse;
import java.net.URI;
import java.util.UUID;
@RestController
@RequestMapping("/credentials")
@RequiredArgsConstructor
@Tag(name = "Credentials (internal)", description = "Service-to-service only; requires X-Internal-Key")
public class CredentialController {

    private final CredentialService credentialService;

    // ---- POST section ------------------------------------------------------

    @Operation(summary = "Create credentials for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Not an internal call"),
            @ApiResponse(responseCode = "409", description = "Username already taken")})

    @PostMapping
    @PreAuthorize("hasRole('INTERNAL')")
    public ResponseEntity<CredentialResponse> create(@Valid @RequestBody CreateCredentialRequest request) {
        CredentialResponse created = credentialService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.uuid())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // ---- PUT section -------------------------------------------------------

    @Operation(summary = "Update username and/or password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "403", description = "Not an internal call"),
            @ApiResponse(responseCode = "404", description = "Credential not found"),
            @ApiResponse(responseCode = "409", description = "Username already taken")})

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('INTERNAL')")
    public ResponseEntity<CredentialResponse> update(@Parameter(description = "User UUID") @PathVariable UUID userId,
                                               @Valid @RequestBody UpdateCredentialsRequest request) {
        return ResponseEntity.ok(credentialService.update(userId, request));
    }

    // ---- DELETE section ----------------------------------------------------

    @Operation(summary = "Delete a user's credentials")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "403", description = "Not an internal call"),
            @ApiResponse(responseCode = "404", description = "Credential not found")})

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('INTERNAL')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCredentials(@Parameter(description = "User UUID") @PathVariable UUID userId) {
        credentialService.delete(userId);
    }
}
