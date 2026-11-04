package com.retiantonio.user_service.controller;

import com.retiantonio.user_service.dto.request.CreateUserRequest;
import com.retiantonio.user_service.dto.request.UpdateUserRequest;
import com.retiantonio.user_service.dto.response.UserResponse;
import com.retiantonio.user_service.service.UserService;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management")
public class UserController {

    private final UserService userService;

    // ---- POST section ------------------------------------------------------

    @Operation(summary = "Create a user", description = "Creates the profile and its credentials. ADMIN or INTERNAL.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created (Location header set)"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "409", description = "E-mail or username already taken")})

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

    @Operation(summary = "Update a user", description = "Partial or full update; null/blank fields are ignored. ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Updated"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "409", description = "E-mail or username already taken")})

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> update(
                                @Parameter(description = "User UUID") @PathVariable UUID uuid,
                               @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(uuid, request));
    }

    // ---- DELETE section ----------------------------------------------------

    @Operation(summary = "Delete a user", description = "Also unassigns their devices and deletes their credentials. ADMIN only.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "404", description = "User not found")})

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Parameter(description = "User UUID") @PathVariable UUID uuid) {
        userService.delete(uuid);
    }

    // ---- GET section -------------------------------------------------------

    @Operation(summary = "Search users", description = "ADMIN/INTERNAL see everyone; CLIENT only sees their own profile.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of matching users"),
            @ApiResponse(responseCode = "403", description = "Client asked for another user's profile")})

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','CLIENT','INTERNAL')")
    public ResponseEntity<List<UserResponse>> getUsers(
            @Parameter(description = "Filter by user UUID") @RequestParam(required = false) UUID id,
            @Parameter(description = "Filter by e-mail") @RequestParam(required = false) String email,
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(hidden = true) Authentication auth) {

        return ResponseEntity.ok(userService.search(id, email, name, auth));
    }
}
