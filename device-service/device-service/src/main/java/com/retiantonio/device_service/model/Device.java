package com.retiantonio.device_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "devices")

@Getter @Setter @NoArgsConstructor
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "A name for the device is required.")
    private String name;

    private String description;

    @NotNull
    @Column(name = "max_energy_consumption", nullable = false)
    private double maxEnergyConsumption;

    @Column(name = "owner_user_id")
    private UUID ownerUserId;
}