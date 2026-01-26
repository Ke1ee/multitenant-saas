package com.kelee.multitenantsaas.tenant.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(
        @NotBlank(message = "name is required") @Size(min = 2, max = 120, message = "name must be between 2 and 120 characters") String name) {
}
