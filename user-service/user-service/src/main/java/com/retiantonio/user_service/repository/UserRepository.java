package com.retiantonio.user_service.repository;

import com.retiantonio.user_service.model.UserAbstract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserAbstract, UUID>, JpaSpecificationExecutor<UserAbstract> {

    Optional<UserAbstract> findById(UUID uuid);
    Optional<UserAbstract> findByEmail(String email);

    boolean existsById(UUID uuid);
    boolean existsByEmail(String email);
    boolean existsByEmailIgnoreCase(String email);
}
