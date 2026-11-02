package com.retiantonio.authentication_service.service;

import com.retiantonio.authentication_service.dto.request.CreateCredentialRequest;
import com.retiantonio.authentication_service.dto.request.UpdateCredentialsRequest;
import com.retiantonio.authentication_service.dto.response.CredentialResponse;
import com.retiantonio.authentication_service.exception.AlreadyTakenException;
import com.retiantonio.authentication_service.exception.CredentialNotFoundException;
import com.retiantonio.authentication_service.model.Credential;
import com.retiantonio.authentication_service.repository.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CredentialService {

    private final CredentialRepository credentialRepository;

    // beans
    private final PasswordEncoder passwordEncoder;

    // ---- POST section ------------------------------------------------------

    @Transactional
    public CredentialResponse create(CreateCredentialRequest request) {

        if(credentialRepository.existsByUsername(request.username())) {
            throw new AlreadyTakenException("Username already taken.");
        }

        Credential credential = new Credential();
        credential.setUsername(request.username());
        credential.setPasswordHash(passwordEncoder.encode(request.password()));
        credential.setRole(request.role());
        credential.setUserId(request.userId());

        Credential saved = credentialRepository.save(credential);
        return toResponse(saved);
    }

    // ---- PUT section -------------------------------------------------------

    @Transactional
    public CredentialResponse update(UUID userId, UpdateCredentialsRequest request) {
       Credential credential = findOrThrowByUserId(userId);

        if (request.username() != null && !request.username().equals(credential.getUsername())) {
            if (credentialRepository.existsByUsername(request.username()))
                throw new AlreadyTakenException("Username already taken.");
            credential.setUsername(request.username());
        }

        if (request.newPassword() != null) {
            credential.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        }
        return toResponse(credential);
    }

    // ---- DELETE section ----------------------------------------------------

    @Transactional
    public void delete(UUID userId) {
        Credential credential = findOrThrowByUserId(userId);
        credentialRepository.delete(credential);
    }

    // ---- HELPERS section ---------------------------------------------------

    private Credential findOrThrowByUserId(UUID userId) {
        return credentialRepository.findByUserId(userId)
                .orElseThrow(() -> new CredentialNotFoundException("Credential not found for user: " + userId));
    }

    private CredentialResponse toResponse(Credential credential) {
        return new CredentialResponse(
                credential.getId(),
                credential.getUsername(),
                credential.getRole(),
                credential.getUserId()
        );
    }
}
