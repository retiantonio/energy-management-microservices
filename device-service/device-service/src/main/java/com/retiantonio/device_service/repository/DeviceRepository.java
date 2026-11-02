package com.retiantonio.device_service.repository;

import com.retiantonio.device_service.model.Device;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID>, JpaSpecificationExecutor<Device> {

    List<Device> findByOwnerUserId(UUID ownerUserId);

    @Modifying
    @Query("UPDATE Device d SET d.ownerUserId = null WHERE d.ownerUserId = :userId")
    void unassignAllFrom(@Param("userId") UUID userId);
}

