package io.teammetric.tracker.controller;

import io.teammetric.tracker.dto.ProjectDto;
import io.teammetric.tracker.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectService projectService;

    @GetMapping("/{id}")
    public ProjectDto getById(@PathVariable("id") Long id) {
        return projectService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.save(projectDto);
    }

    @PutMapping("/{id}")
    public ProjectDto update(@PathVariable("id") Long id, @Valid @RequestBody ProjectDto projectDto) {
        return projectService.update(id, projectDto);
    }
}
