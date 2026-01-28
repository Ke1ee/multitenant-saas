package com.kelee.multitenantsaas.tenant.api;

import com.kelee.multitenantsaas.config.TenantAlreadyExistsException;
import com.kelee.multitenantsaas.config.TenantNotFoundException;
import com.kelee.multitenantsaas.tenant.Tenant;
import com.kelee.multitenantsaas.tenant.TenantRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
            throw new TenantAlreadyExistsException(normalizedName);
        }

        Tenant tenant = tenantRepository.save(new Tenant(normalizedName));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(tenant));
    }

    @GetMapping
    public List<TenantResponse> listTenants() {
        return tenantRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public TenantResponse getTenant(@PathVariable UUID id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new TenantNotFoundException(id));
        return toResponse(tenant);
    }

    private TenantResponse toResponse(Tenant tenant) {
        return new TenantResponse(tenant.getId(), tenant.getName(), tenant.getCreatedAt());
    }
}