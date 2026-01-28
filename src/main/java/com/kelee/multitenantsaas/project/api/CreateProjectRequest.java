package com.kelee.multitenantsaas.project.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @NotBlank(message = "name is required") @Size(min = 2, max = 120, message = "name must be between 2 and 120 characters") String name,

        String description) {
}