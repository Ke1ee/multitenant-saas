package com.kelee.multitenantsaas.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findAllByProjectIdAndTenantId(UUID projectId, UUID tenantId);

    Optional<Task> findByIdAndTenantId(UUID id, UUID tenantId);
}