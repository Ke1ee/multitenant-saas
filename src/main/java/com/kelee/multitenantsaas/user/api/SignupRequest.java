package com.kelee.multitenantsaas.user.api;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SignupRequest(
        @NotNull(message = "tenantId is required") UUID tenantId,

        @NotBlank(message = "email is required") @Email(message = "email must be valid") String email,

        @NotBlank(message = "password is required") @Size(min = 8, message = "password must be at least 8 characters") String password) {
}