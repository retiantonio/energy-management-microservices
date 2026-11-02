package com.retiantonio.authentication_service.controller;

import com.retiantonio.authentication_service.dto.request.LoginRequest;
import com.retiantonio.authentication_service.dto.request.RegisterRequest;
import com.retiantonio.authentication_service.dto.response.AuthResponse;
import com.retiantonio.authentication_service.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/validate")
    public ResponseEntity<Void> validate(Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority().substring("ROLE_".length());
        return ResponseEntity.ok()
                .header("X-User-Id", auth.getName())
                .header("X-User-Role", role)
                .build();
    }

}
