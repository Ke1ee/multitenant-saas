package com.kelee.multitenantsaas.project.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(
        @NotBlank(message = "title is required") @Size(min = 2, max = 255, message = "title must be between 2 and 255 characters") String title,

        String description) {
}