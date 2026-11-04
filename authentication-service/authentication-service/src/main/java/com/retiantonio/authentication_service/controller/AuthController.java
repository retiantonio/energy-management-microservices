package com.retiantonio.authentication_service.controller;

import com.retiantonio.authentication_service.dto.request.LoginRequest;
import com.retiantonio.authentication_service.dto.request.RegisterRequest;
import com.retiantonio.authentication_service.dto.response.AuthResponse;
import com.retiantonio.authentication_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login and token validation")
public class AuthController {

    private final AuthService authService;

    @SecurityRequirements   // empty = public, no lock icon
    @Operation(summary = "Register a new CLIENT account", description = "Creates the profile and credentials, returns a valid JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registered"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "E-mail or username already taken")})

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @SecurityRequirements
    @Operation(summary = "Login", description = "Returns a valid JWT token.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Authenticated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Bad credentials")})

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Validate a token", description = "Used by Traefik ForwardAuth. Returns X-User-Id and X-User-Role headers.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Valid"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid token")})

    @GetMapping("/validate")
    public ResponseEntity<Void> validate(@Parameter(hidden = true) Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority().substring("ROLE_".length());
        return ResponseEntity.ok()
                .header("X-User-Id", auth.getName())
                .header("X-User-Role", role)
                .build();
    }
}
