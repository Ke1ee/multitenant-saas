package com.kelee.multitenantsaas.project.api;

import java.time.Instant;
import java.util.UUID;

public record TaskResponse(
        UUID id,
        UUID projectId,
        String title,
        String description,
        String status,
        Instant createdAt) {
}