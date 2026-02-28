package io.teammetric.tracker.service;

import io.teammetric.tracker.dto.request.project.CreateProjectRequest;
import io.teammetric.tracker.dto.request.project.UpdateProjectRequest;
import io.teammetric.tracker.dto.response.project.ProjectResponse;
import io.teammetric.tracker.entity.Project;
import io.teammetric.tracker.exception.EntityNotFoundException;
import io.teammetric.tracker.mapper.project.ProjectMapper;
import io.teammetric.tracker.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectMapper projectMapper;

    @InjectMocks
    private ProjectService projectService;

    @Test
    @DisplayName("GetById: Если проект найден — он должен быть возвращён в виде ProjectResponse")
    void getById_WhenProjectExists_ShouldReturnProjectResponse() {
        // --- GIVEN ---
        Long id = 1L;
        String name = "Project name";

        ProjectResponse expectedDto = ProjectResponse.builder()
                .id(id)
                .name(name)
                .build();

        Project existingProject = Project.builder()
                .id(id)
                .name(name)
                .build();

        when(projectRepository.findById(id)).thenReturn(Optional.of(existingProject));
        when(projectMapper.toResponse(existingProject)).thenReturn(expectedDto);

        // --- WHEN ---
        ProjectResponse actualDto = projectService.getById(id);

        // --- THEN ---
        assertThat(actualDto)
                .isNotNull()
                .isEqualTo(expectedDto);
    }

    @Test
    @DisplayName("GetById: Если проект не найден — должно быть выброшено исключение")
    void getById_WhenProjectNotFound_ShouldThrowException() {
        // --- GIVEN ---
        Long id = 99L;
        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        // --- WHEN & THEN ---
        assertThatThrownBy(() -> projectService.getById(id))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found with id: " + id);
    }

    @Test
    @DisplayName("FindAll: Если проекты существуют — должен вернуть список ProjectResponse")
    void findAll_WhenProjectsExist_ShouldReturnList() {
        // --- GIVEN ---
        Project first = Project.builder()
                .id(1L)
                .name("First")
                .build();

        Project second = Project.builder()
                .id(2L)
                .name("Second")
                .build();

        ProjectResponse firstProjectResponse = ProjectResponse.builder()
                .id(1L)
                .name("First")
                .build();

        ProjectResponse secondProjectResponse = ProjectResponse.builder()
                .id(2L)
                .name("Second")
                .build();

        List<Project> projectList = List.of(first, second);
        when(projectRepository.findAll()).thenReturn(projectList);
        when(projectMapper.toResponse(first)).thenReturn(firstProjectResponse);
        when(projectMapper.toResponse(second)).thenReturn(secondProjectResponse);

        // --- WHEN ---
        List<ProjectResponse> actualList = projectService.findAll();

        // --- THEN ---
        assertThat(actualList)
                .isNotNull()
                .hasSize(2);
    }

    @Test
    @DisplayName("FindAll: Если проектов нет — должен вернуть пустой список")
    void findAll_WhenProjectsNotFound_ShouldReturnEmptyList() {
        // --- GIVEN ---
        when(projectRepository.findAll()).thenReturn(List.of());

        // --- WHEN ---
        List<ProjectResponse> actualList = projectService.findAll();

        // --- THEN ---
        assertThat(actualList)
                .isNotNull()
                .isEmpty();
    }

    @Test
    @DisplayName("Save: При вызове метода 1 раз должен вызваться репозиторий")
    void save_WhenCalled_ShouldCallRepository() {
        // --- GIVEN ---
        Long id = 1L;
        String name = "Prostokvashino";
        String description = "Fictional village";

        CreateProjectRequest requestDto = CreateProjectRequest.builder()
                .name(name)
                .description(description)
                .build();

        ProjectResponse responseDto = ProjectResponse.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        Project projectToSave = Project.builder()
                .name(name)
                .description(description)
                .build();

        Project savedProject = Project.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        when(projectMapper.toEntity(requestDto)).thenReturn(projectToSave);
        when(projectRepository.save(projectToSave)).thenReturn(savedProject);
        when(projectMapper.toResponse(savedProject)).thenReturn(responseDto);

        // --- WHEN ---
        ProjectResponse actualDto = projectService.save(requestDto);

        // --- THEN ---
        assertThat(actualDto).isNotNull();
        verify(projectRepository).save(projectToSave);
    }

    @Test
    @DisplayName("Update: При вызове метода 1 раз должен вызваться репозиторий")
    void update_WhenCalled_ShouldCallRepository() {
        // --- GIVEN ---
        Long id = 2L;
        String name = "Coffee";
        String description = "I want this!";

        UpdateProjectRequest requestDto = UpdateProjectRequest.builder()
                .name(name)
                .description(description)
                .build();

        ProjectResponse responseDto = ProjectResponse.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        Project existingProject = Project.builder()
                .id(id)
                .name(name)
                .description(description)
                .build();

        when(projectRepository.findById(id)).thenReturn(Optional.of(existingProject));
        when(projectRepository.save(existingProject)).thenReturn(existingProject);
        when(projectMapper.toResponse(existingProject)).thenReturn(responseDto);

        // --- WHEN ---
        ProjectResponse actualDto = projectService.update(id, requestDto);

        // --- THEN ---
        assertThat(actualDto).isNotNull();
        verify(projectRepository).save(existingProject);
    }

    @Test
    @DisplayName("Update: Если проект не найден — должно быть выброшено исключение")
    void update_WhenProjectNotFound_ShouldThrowException() {
        // --- GIVEN ---
        Long id = 99L;
        UpdateProjectRequest requestDto = UpdateProjectRequest.builder()
                .name("Dumplings")
                .description("My precious!")
                .build();

        when(projectRepository.findById(id)).thenReturn(Optional.empty());

        // --- WHEN & THEN ---
        assertThatThrownBy(() -> projectService.update(id, requestDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Project not found with id: " + id);
    }
}
