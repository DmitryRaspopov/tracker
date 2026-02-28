package io.teammetric.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.teammetric.tracker.dto.request.project.CreateProjectRequest;
import io.teammetric.tracker.dto.request.project.UpdateProjectRequest;
import io.teammetric.tracker.dto.response.project.ProjectResponse;
import io.teammetric.tracker.exception.EntityNotFoundException;
import io.teammetric.tracker.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Test
    @DisplayName("GetById: Если проект найден — должен вернуться статус 200 и JSON с данными")
    void getById_WhenProjectExists_ShouldReturnStatusOk() throws Exception {
        // --- GIVEN ---
        Long id = 1L;
        String name = "Mayonnaise";

        ProjectResponse responseDto = ProjectResponse.builder()
                .id(id)
                .name(name)
                .build();

        when(projectService.getById(id)).thenReturn(responseDto);

        // --- WHEN & THEN ---
        mockMvc.perform(get("/api/projects/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @DisplayName("GetById: Если проект не найден — должен вернуться статус 404")
    void getById_WhenProjectNotFound_ShouldThrowException() throws Exception {
        // --- GIVEN ---
        Long id = 99L;

        when(projectService.getById(id))
                .thenThrow(new EntityNotFoundException("Project not found with id: " + id));

        // --- WHEN & THEN ---
        mockMvc.perform(get("/api/projects/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("FindAll: Если проекты существуют — должен вернуться не пустой список проектов и статус 200")
    void findAll_WhenProjectsExist_ShouldReturnListAndStatusOk() throws Exception {
        // --- GIVEN ---
        ProjectResponse firstResponse = ProjectResponse.builder()
                .id(1L)
                .name("Ketchup")
                .build();

        ProjectResponse secondResponse = ProjectResponse.builder()
                .id(2L)
                .name("Caramel milk latte")
                .build();

        List<ProjectResponse> projectResponseList = List.of(firstResponse, secondResponse);
        when(projectService.findAll()).thenReturn(projectResponseList);

        // --- WHEN & THEN ---
        mockMvc.perform(get("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(projectResponseList.size()))
                .andExpect(jsonPath("$.[0].id").value(firstResponse.id()))
                .andExpect(jsonPath("$.[0].name").value(firstResponse.name()))
                .andExpect(jsonPath("$.[1].id").value(secondResponse.id()))
                .andExpect(jsonPath("$.[1].name").value(secondResponse.name()));
    }

    @Test
    @DisplayName("FindAll: Если проектов нет — должен вернуть пустой список и статус 200")
    void findAll_WhenProjectsNotFound_ShouldReturnEmptyListAndStatusOk() throws Exception {
        // --- GIVEN ---
        when(projectService.findAll()).thenReturn(List.of());

        // --- WHEN & THEN ---
        mockMvc.perform(get("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @DisplayName("Create: При создании проекта с валидными данными — должен вернуться статус 201 и JSON с данными")
    void create_WhenValidInput_ShouldReturnStatusCreated() throws Exception {
        // --- GIVEN ---
        Long id = 1L;
        String name = "Sour cream";

        CreateProjectRequest requestDto = CreateProjectRequest.builder()
                .name(name)
                .build();

        ProjectResponse responseDto = ProjectResponse.builder()
                .id(id)
                .name(name)
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        when(projectService.save(any(CreateProjectRequest.class))).thenReturn(responseDto);

        // --- WHEN & THEN ---
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @DisplayName("Create: При создании проекта с невалидными данными — должен вернуться статус 400")
    void create_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // --- GIVEN ---
        CreateProjectRequest requestDto = CreateProjectRequest.builder()
                .name("")
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        // --- WHEN & THEN ---
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(projectService);
    }

    @Test
    @DisplayName("Update: При обновлении проекта с валидными данными — должен вернуться статус 200 и JSON с данными")
    void update_WhenValidInput_ShouldReturnStatusOk() throws Exception {
        // --- GIVEN ---
        Long id = 3L;
        String name = "Sandwich";

        UpdateProjectRequest requestDto = UpdateProjectRequest.builder()
                .name(name)
                .build();

        ProjectResponse responseDto = ProjectResponse.builder()
                .id(id)
                .name(name)
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        when(projectService.update(eq(id), any(UpdateProjectRequest.class))).thenReturn(responseDto);

        // --- WHEN & THEN ---
        mockMvc.perform(put("/api/projects/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @DisplayName("Update: При обновлении проекта с невалидными данными — должен вернуться статус 400")
    void update_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // --- GIVEN ---
        UpdateProjectRequest requestDto = UpdateProjectRequest.builder()
                .name("")
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        // --- WHEN & THEN ---
        mockMvc.perform(put("/api/projects/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(projectService);
    }

    @Test
    @DisplayName("Update: При обновлении несуществующего проекта — должен вернуться статус 404")
    void update_WhenProjectNotFound_ShouldThrowException() throws Exception {
        // --- GIVEN ---
        Long id = 99L;

        UpdateProjectRequest requestDto = UpdateProjectRequest.builder()
                .name("Gobbledygook")
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        when(projectService.update(eq(id), any(UpdateProjectRequest.class)))
                .thenThrow(new EntityNotFoundException("Project not found with id: " + id));

        // --- WHEN & THEN ---
        mockMvc.perform(put("/api/projects/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }
}
