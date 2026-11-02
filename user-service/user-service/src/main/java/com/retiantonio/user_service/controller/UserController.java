package com.retiantonio.user_service.controller;

import com.retiantonio.user_service.dto.request.CreateUserRequest;
import com.retiantonio.user_service.dto.request.UpdateUserRequest;
import com.retiantonio.user_service.dto.response.UserResponse;
import com.retiantonio.user_service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // ---- POST section ------------------------------------------------------

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','INTERNAL')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
        UserResponse created = userService.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.uuid())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    // ---- PUT section -------------------------------------------------------

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> update(@PathVariable UUID uuid,
                               @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(uuid, request));
    }

    // ---- DELETE section ----------------------------------------------------

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        userService.delete(uuid);
    }

    // ---- GET section -------------------------------------------------------

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','INTERNAL')")
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String name,
            Authentication auth) {

        return ResponseEntity.ok(userService.search(id, email, name, auth));
    }
}
