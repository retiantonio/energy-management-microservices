package com.retiantonio.authentication_service.service;

import com.retiantonio.authentication_service.component.UserClient;
import com.retiantonio.authentication_service.dto.request.CreateCredentialRequest;
import com.retiantonio.authentication_service.dto.request.CreateUserRequest;
import com.retiantonio.authentication_service.dto.request.LoginRequest;
import com.retiantonio.authentication_service.dto.request.RegisterRequest;
import com.retiantonio.authentication_service.dto.response.AuthResponse;
import com.retiantonio.authentication_service.dto.response.CredentialResponse;
import com.retiantonio.authentication_service.exception.CredentialNotFoundException;
import com.retiantonio.authentication_service.model.Credential;
import com.retiantonio.authentication_service.model.Role;
import com.retiantonio.authentication_service.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    // locals
    private final CredentialRepository credentialRepository;
    private final JwtService jwtService;
    private final CredentialService credentialService;

    // beans
    private final AuthenticationManager authenticationManager;

    // other microservices
    private final UserClient userClient;

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        UUID userId = userClient.createUser(new CreateUserRequest(
                Role.CLIENT,
                request.username(),
                request.password(),
                request.name(),
                request.email(),
                request.address())
        );

        Credential credential = credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new CredentialNotFoundException("Credential not found for user: " + userId));

        String token = jwtService.generate(credential.getUserId(), credential.getRole());
        return toResponse(token, credential);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String username = request.username();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username, request.password())
        );

        Credential credential = credentialRepository.findByUsername(username)
                .orElseThrow(() -> new CredentialNotFoundException("Credential not found for user: " + username));

        String token = jwtService.generate(credential.getUserId(), credential.getRole());
        return toResponse(token, credential);
    }

    // ---- HELPERS SECTION ---------------------------------------------------

    private AuthResponse toResponse(String token, Credential credential) {
        return new AuthResponse(token, credential.getUsername(), credential.getUserId(), credential.getRole(), jwtService.expiresInSeconds());
    }
}
