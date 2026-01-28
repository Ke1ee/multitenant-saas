package com.kelee.multitenantsaas.project.api;

import com.kelee.multitenantsaas.config.ProjectNotFoundException;
import com.kelee.multitenantsaas.project.Project;
import com.kelee.multitenantsaas.project.ProjectRepository;
import com.kelee.multitenantsaas.tenant.Tenant;
import com.kelee.multitenantsaas.tenant.TenantContext;
import com.kelee.multitenantsaas.tenant.TenantRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;
    private final TenantRepository tenantRepository;

    public ProjectController(ProjectRepository projectRepository, TenantRepository tenantRepository) {
        this.projectRepository = projectRepository;
        this.tenantRepository = tenantRepository;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody CreateProjectRequest request) {
        UUID tenantId = TenantContext.getTenantId();
        Tenant tenant = tenantRepository.findById(tenantId).orElseThrow();

        Project project = new Project(tenant, request.name(), request.description());
        projectRepository.save(project);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(project));
    }

    @GetMapping
    public List<ProjectResponse> listProjects() {
        UUID tenantId = TenantContext.getTenantId();
        return projectRepository.findAllByTenantId(tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ProjectResponse getProject(@PathVariable UUID id) {
        UUID tenantId = TenantContext.getTenantId();
        Project project = projectRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        return toResponse(project);
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt());
    }
}