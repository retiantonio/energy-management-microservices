package com.retiantonio.user_service.service;

import com.retiantonio.user_service.component.CredentialClient;
import com.retiantonio.user_service.component.DeviceClient;
import com.retiantonio.user_service.dto.request.CreateCredentialRequest;
import com.retiantonio.user_service.dto.request.CreateUserRequest;
import com.retiantonio.user_service.dto.request.UpdateCredentialRequest;
import com.retiantonio.user_service.dto.request.UpdateUserRequest;
import com.retiantonio.user_service.dto.response.UserResponse;
import com.retiantonio.user_service.exception.AlreadyTakenException;
import com.retiantonio.user_service.exception.UserNotFoundException;
import com.retiantonio.user_service.model.Administrator;
import com.retiantonio.user_service.model.Client;
import com.retiantonio.user_service.model.Role;
import com.retiantonio.user_service.model.UserAbstract;
import com.retiantonio.user_service.repository.UserRepository;

import com.retiantonio.user_service.repository.UserSpecs;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final DeviceClient deviceClient;
    private final CredentialClient credentialClient;

    // ---- POST section ------------------------------------------------------

    @Transactional
    public UserResponse create(CreateUserRequest request) {

        if(userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new AlreadyTakenException("E-mail already taken.");
        }

        UserAbstract user = switch (request.role()) {
            case CLIENT -> new Client();
            case ADMIN -> new Administrator();
        };

        user.setName(request.name());
        user.setEmail(request.email());
        user.setAddress(request.address());
        UserAbstract saved = userRepository.save(user);

        credentialClient.createCredential(new CreateCredentialRequest(
                request.username(),
                request.password(),
                request.role(),
                saved.getUuid())
        );

        return toResponse(saved);
    }

    // ---- PUT section -------------------------------------------------------

    @Transactional
    public UserResponse update(UUID uuid, UpdateUserRequest request) {

        UserAbstract user = findOrThrow(uuid);

        if (hasText(request.name())) {
            user.setName(request.name().trim());
        }

        if (hasText(request.email()) && !request.email().trim().equalsIgnoreCase(user.getEmail())) {
            String email = request.email().trim();
            if (userRepository.existsByEmailIgnoreCase(email)) {
                throw new AlreadyTakenException("E-mail is already taken.");
            }
            user.setEmail(email);
        }

        if (hasText(request.address())) {
            user.setAddress(request.address().trim());
        }

        credentialClient.updateCredentialFor(uuid, new UpdateCredentialRequest(
                request.username(),
                request.password())
        );

        return toResponse(user);
    }

    // ---- DELETE section ----------------------------------------------------

    @Transactional
    public void delete(UUID uuid) {
        UserAbstract toDeleteUser = findOrThrow(uuid);

        userRepository.delete(toDeleteUser);

        deviceClient.unassignAllFrom(uuid);
        credentialClient.deleteCredentialFrom(uuid);
    }

    // ---- GET section -------------------------------------------------------

    public List<UserResponse> search(UUID id, String email, String name, Authentication auth) {
        boolean privileged = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_INTERNAL"));

        UUID effectiveId = id;
        if (!privileged) {
            UUID self = UUID.fromString(auth.getName());
            if (id != null && !id.equals(self)) {
                throw new AccessDeniedException("Clients can only view their own profile.");
            }
            effectiveId = self;
        }

        return userRepository.findAll(UserSpecs.filter(effectiveId, email, name))
                .stream().map(this::toResponse).toList();
    }

    // ---- HELPERS section ---------------------------------------------------

    private UserAbstract findOrThrow(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + id + "."));
    }

    private UserResponse toResponse(UserAbstract saved) {
        Role role = (saved instanceof Administrator) ? Role.ADMIN : Role.CLIENT;
        return new UserResponse(
            saved.getUuid(),
            saved.getName(),
            saved.getEmail(),
            saved.getAddress(),
            role
        );
    }

    private static boolean hasText(String s) {
        return s != null && !s.isBlank();
    }
}
