package com.retiantonio.authentication_service.controller;

import com.retiantonio.authentication_service.dto.request.CreateCredentialRequest;
import com.retiantonio.authentication_service.dto.request.UpdateCredentialsRequest;
import com.retiantonio.authentication_service.dto.response.CredentialResponse;
import com.retiantonio.authentication_service.model.Credential;
import com.retiantonio.authentication_service.service.CredentialService;
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
public class CredentialController {

    private final CredentialService credentialService;

    // ---- POST section ------------------------------------------------------

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

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('INTERNAL')")
    public ResponseEntity<CredentialResponse> update(@PathVariable UUID userId,
                                               @Valid @RequestBody UpdateCredentialsRequest request) {
        return ResponseEntity.ok(credentialService.update(userId, request));
    }

    // ---- DELETE section ----------------------------------------------------

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('INTERNAL')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCredentials(@PathVariable UUID userId) {
        credentialService.delete(userId);
    }
}
