package com.kelee.multitenantsaas.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findAllByTenantId(UUID tenantId);

    Optional<Project> findByIdAndTenantId(UUID id, UUID tenantId);
}
