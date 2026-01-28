package com.kelee.multitenantsaas.project.api;

import com.kelee.multitenantsaas.config.ProjectNotFoundException;
import com.kelee.multitenantsaas.config.TaskNotFoundException;
import com.kelee.multitenantsaas.project.*;
import com.kelee.multitenantsaas.tenant.TenantContext;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/tasks")
public class TaskController {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskController(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request) {

        UUID tenantId = TenantContext.getTenantId();

        Project project = projectRepository.findByIdAndTenantId(projectId, tenantId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        Task task = new Task(project, project.getTenant(), request.title(), request.description());
        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(task));
    }

    @GetMapping
    public List<TaskResponse> listTasks(@PathVariable UUID projectId) {
        UUID tenantId = TenantContext.getTenantId();

        // Verify project belongs to tenant
        projectRepository.findByIdAndTenantId(projectId, tenantId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId));

        return taskRepository.findAllByProjectIdAndTenantId(projectId, tenantId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/{taskId}")
    public TaskResponse getTask(@PathVariable UUID projectId, @PathVariable UUID taskId) {
        UUID tenantId = TenantContext.getTenantId();

        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        return toResponse(task);
    }

    @PatchMapping("/{taskId}/status")
    public TaskResponse updateTaskStatus(
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskStatusRequest request) {

        UUID tenantId = TenantContext.getTenantId();

        Task task = taskRepository.findByIdAndTenantId(taskId, tenantId)
                .orElseThrow(() -> new TaskNotFoundException(taskId));

        task.setStatus(TaskStatus.valueOf(request.status()));
        taskRepository.save(task);

        return toResponse(task);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus().name(),
                task.getCreatedAt());
    }
}