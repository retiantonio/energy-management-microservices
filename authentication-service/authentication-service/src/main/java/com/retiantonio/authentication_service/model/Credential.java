package com.retiantonio.authentication_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "credentials")
@Getter @Setter @NoArgsConstructor
public class Credential {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "Username is required.")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password is required.")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotNull(message = "Role is required.")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @NotNull(message = "No user to link provided.")
    @Column(name = "user_id", unique = true)
    private UUID userId;
}
