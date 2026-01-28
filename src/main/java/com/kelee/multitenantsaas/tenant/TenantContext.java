package com.kelee.multitenantsaas.tenant;

import java.util.UUID;

public class TenantContext {

    private static final ThreadLocal<UUID> currentTenantId = new ThreadLocal<>();

    public static UUID getTenantId() {
        return currentTenantId.get();
    }

    public static void setTenantId(UUID tenantId) {
        currentTenantId.set(tenantId);
    }

    public static void clear() {
        currentTenantId.remove();
    }
}