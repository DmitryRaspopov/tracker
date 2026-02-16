package io.teammetric.tracker.service;

import io.teammetric.tracker.dto.ProjectDto;
import io.teammetric.tracker.entity.Project;
import io.teammetric.tracker.exception.EntityNotFoundException;
import io.teammetric.tracker.mapper.ProjectMapper;
import io.teammetric.tracker.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectDto getById(Long id) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Project not found with id: " + id)
        );

        return projectMapper.toDto(project);
    }

    public List<ProjectDto> findAll() {
        List<Project> projects = projectRepository.findAll();
        List<ProjectDto> projectDtoList = new ArrayList<>(projects.size());

        for (Project project : projects) {
            projectDtoList.add(projectMapper.toDto(project));
        }

        return projectDtoList;
    }

    @Transactional
    public ProjectDto save(ProjectDto projectDto) {
        Project project = projectMapper.toProject(projectDto);
        Project savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
    }

    @Transactional
    public ProjectDto update(Long id, ProjectDto projectDetails) {
        ProjectDto projectDto = getById(id);

        projectDto.setName(projectDetails.getName());
        projectDto.setDescription(projectDetails.getDescription());
        projectDto.setEmployees(projectDetails.getEmployees());

        Project project = projectMapper.toProject(projectDto);
        Project savedProject = projectRepository.save(project);

        return projectMapper.toDto(savedProject);
    }
}
