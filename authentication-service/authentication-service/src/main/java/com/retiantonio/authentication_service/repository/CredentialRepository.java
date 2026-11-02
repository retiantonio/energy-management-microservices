package com.retiantonio.authentication_service.repository;

import com.retiantonio.authentication_service.model.Credential;
import com.retiantonio.authentication_service.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, UUID> {

    Optional<Credential> findByUsername(String username);
    Optional<Credential> findByUserId(UUID userId);

    boolean existsByUsername(String username);
    boolean existsByRole(Role role);

    void deleteByUserId(UUID userId);
}
