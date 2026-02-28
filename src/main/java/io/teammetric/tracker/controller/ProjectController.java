package io.teammetric.tracker.controller;

import io.teammetric.tracker.dto.request.project.CreateProjectRequest;
import io.teammetric.tracker.dto.request.project.UpdateProjectRequest;
import io.teammetric.tracker.dto.response.project.ProjectResponse;
import io.teammetric.tracker.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ProjectResponse getById(@PathVariable("id") Long id) {
        return projectService.getById(id);
    }

    @GetMapping
    public List<ProjectResponse> findAll() {
        return projectService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest projectRequest) {
        return projectService.save(projectRequest);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable("id") Long id, @Valid @RequestBody UpdateProjectRequest projectRequest) {
        return projectService.update(id, projectRequest);
    }
}
