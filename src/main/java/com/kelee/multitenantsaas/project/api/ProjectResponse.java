package com.kelee.multitenantsaas.project.api;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
        UUID id,
        String name,
        String description,
        Instant createdAt) {
}