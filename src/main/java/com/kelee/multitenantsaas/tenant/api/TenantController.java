package com.kelee.multitenantsaas.tenant.api;

import com.kelee.multitenantsaas.tenant.Tenant;
import com.kelee.multitenantsaas.tenant.TenantRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenants")
public class TenantController {

    private final TenantRepository tenantRepository;

    public TenantController(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @PostMapping
    public ResponseEntity<TenantResponse> createTenant(@Valid @RequestBody CreateTenantRequest request) {
        String normalizedName = request.name().trim();

        if (tenantRepository.existsByNameIgnoreCase(normalizedName)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Tenant tenant = tenantRepository.save(new Tenant(normalizedName));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TenantResponse(tenant.getId(), tenant.getName(), tenant.getCreatedAt()));
    }
}
