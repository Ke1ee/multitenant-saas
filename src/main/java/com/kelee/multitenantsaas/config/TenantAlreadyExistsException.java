package com.kelee.multitenantsaas.config;

public class TenantAlreadyExistsException extends RuntimeException {

    public TenantAlreadyExistsException(String name) {
        super("Tenant already exists: " + name);
    }
}