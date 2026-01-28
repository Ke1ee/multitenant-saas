package com.kelee.multitenantsaas.user.api;

import java.util.UUID;

public record AuthResponse(
        String token,
        UUID userId,
        UUID tenantId,
        String email,
        String role) {
}