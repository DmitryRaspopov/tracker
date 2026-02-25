package io.teammetric.tracker.service;

import io.teammetric.tracker.dto.request.project.CreateProjectRequest;
import io.teammetric.tracker.dto.request.project.UpdateProjectRequest;
import io.teammetric.tracker.dto.response.project.ProjectResponse;
import io.teammetric.tracker.entity.Project;
import io.teammetric.tracker.exception.EntityNotFoundException;
import io.teammetric.tracker.mapper.project.ProjectMapper;
import io.teammetric.tracker.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectResponse getById(Long id) {
        Project project = getProjectById(id);

        return projectMapper.toResponse(project);
    }

    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponse)
                .toList();
    }

    @Transactional
    public ProjectResponse save(CreateProjectRequest projectRequest) {
        Project project = projectMapper.toEntity(projectRequest);
        Project savedProject = projectRepository.save(project);

        return projectMapper.toResponse(savedProject);
    }

    @Transactional
    public ProjectResponse update(Long id, UpdateProjectRequest projectRequest) {
        Project projectToSave = getProjectById(id);

        projectToSave.setName(projectRequest.name());
        projectToSave.setDescription(projectRequest.description());

        Project savedProject = projectRepository.save(projectToSave);

        return projectMapper.toResponse(savedProject);
    }

    private Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Project not found with id: " + id)
        );
    }
}
