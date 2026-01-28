package com.kelee.multitenantsaas.user.api;

import com.kelee.multitenantsaas.tenant.TenantContext;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/me")
public class MeController {

    @GetMapping
    public Map<String, Object> me(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("userId", authentication.getName());
        response.put("tenantId", TenantContext.getTenantId());
        response.put("authorities", authentication.getAuthorities());
        return response;
    }
}