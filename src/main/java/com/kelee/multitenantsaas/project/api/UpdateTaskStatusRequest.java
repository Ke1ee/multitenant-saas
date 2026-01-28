package com.kelee.multitenantsaas.project.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateTaskStatusRequest(
        @NotBlank(message = "status is required") @Pattern(regexp = "TODO|IN_PROGRESS|DONE", message = "status must be TODO, IN_PROGRESS, or DONE") String status) {
}