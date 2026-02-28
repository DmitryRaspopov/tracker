package io.teammetric.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.teammetric.tracker.dto.request.employee.CreateEmployeeRequest;
import io.teammetric.tracker.dto.request.employee.UpdateEmployeeRequest;
import io.teammetric.tracker.dto.response.employee.EmployeeResponse;
import io.teammetric.tracker.exception.EntityNotFoundException;
import io.teammetric.tracker.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Test
    @DisplayName("GetById: Если сотрудник найден — должен вернуться статус 200 и JSON с данными")
    void getById_WhenEmployeeExists_ShouldReturnStatusOk() throws Exception {
        // --- GIVEN ---
        Long id = 1L;
        String firstName = "Владислав";

        EmployeeResponse responseDto = EmployeeResponse.builder()
                .id(id)
                .firstName(firstName)
                .build();

        when(employeeService.getById(id)).thenReturn(responseDto);

        // --- WHEN & THEN ---
        mockMvc.perform(get("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firstName").value(firstName));

    }

    @Test
    @DisplayName("GetById: Если сотрудник не найден — должен вернуться статус 404")
    void getById_WhenEmployeeNotFound_ShouldThrowException() throws Exception {
        // --- GIVEN ---
        Long id = 99L;

        when(employeeService.getById(id))
                .thenThrow(new EntityNotFoundException("Employee not found with id: " + id));

        // --- WHEN & THEN ---
        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("FindAll: Если сотрудники существуют — должен вернуться не пустой список сотрудников и статус 200")
    void findAll_WhenEmployeesExist_ShouldReturnListAndStatusOk() throws Exception {
        // --- GIVEN ---
        EmployeeResponse firstResponse = EmployeeResponse.builder()
                .id(1L)
                .firstName("Маргарита")
                .build();

        EmployeeResponse secondResponse = EmployeeResponse.builder()
                .id(2L)
                .firstName("4 сыра")
                .build();

        List<EmployeeResponse> employeeResponseList = List.of(firstResponse, secondResponse);
        when(employeeService.findAll()).thenReturn(employeeResponseList);

        // WHEN & THEN
        mockMvc.perform(get("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(employeeResponseList.size()))
                .andExpect(jsonPath("$.[0].id").value(firstResponse.id()))
                .andExpect(jsonPath("$.[0].firstName").value(firstResponse.firstName()))
                .andExpect(jsonPath("$.[1].id").value(secondResponse.id()))
                .andExpect(jsonPath("$.[1].firstName").value(secondResponse.firstName()));
    }

    @Test
    @DisplayName("FindAll: Если сотрудников нет — должен вернуть пустой список и статус 200")
    void findAll_WhenEmployeesNotFound_ShouldReturnEmptyListAndStatusOk() throws Exception {
        // --- GIVEN ---
        when(employeeService.findAll()).thenReturn(new ArrayList<>());

        // --- WHEN & THEN
        mockMvc.perform(get("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }

    @Test
    @DisplayName("Create: При создании сотрудника с валидными данными — должен вернуться статус 201 и JSON с данными")
    void create_WhenValidInput_ShouldReturnStatusCreated() throws Exception {
        // --- GIVEN ---
        Long id = 1L;
        String firstName = "Роман";
        String lastName = "Лежепёков";
        String username = "Roman111";
        String email = "roman111@gmail.com";

        CreateEmployeeRequest requestDto = CreateEmployeeRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .build();

        EmployeeResponse responseDto = EmployeeResponse.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        when(employeeService.save(any(CreateEmployeeRequest.class))).thenReturn(responseDto);

        // --- WHEN & THEN
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @DisplayName("Create: При создании сотрудника с невалидными данными — должен вернуться статус 400")
    void create_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        // --- GIVEN ---
        CreateEmployeeRequest requestDto = CreateEmployeeRequest.builder()
                .firstName("")
                .lastName("Лежепёков")
                .username("Roman111")
                .email("roman111@gmail.com")
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        // --- WHEN & THEN
        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(employeeService);
    }

    @Test
    @DisplayName("Update: При обновлении сотрудника с валидными данными — должен вернуться статус 200 и JSON с данными")
    void update_WhenValidInput_ShouldReturnStatusOk() throws Exception {
        // --- GIVEN ---
        Long id = 3L;
        String firstName = "Даниил";
        String lastName = "Иванов";
        String email = "danik333@gmail.com";

        UpdateEmployeeRequest requestDto = UpdateEmployeeRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();

        EmployeeResponse responseDto = EmployeeResponse.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        when(employeeService.update(eq(id), any(UpdateEmployeeRequest.class))).thenReturn(responseDto);

        // --- WHEN & THEN ---
        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @DisplayName("Update: При обновлении сотрудника с невалидными данными — должен вернуться статус 400")
    void update_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        UpdateEmployeeRequest requestDto = UpdateEmployeeRequest.builder()
                .firstName("Дмитрий")
                .lastName("")
                .email("rdmitry@gmail.com")
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/api/employees/{id}", 5)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(employeeService);
    }

    @Test
    @DisplayName("Update: При обновлении несуществующего сотрудника — должен вернуться статус 404")
    void update_WhenEmployeeNotFound_ShouldThrowException() throws Exception {
        // --- GIVEN ---
        Long id = 99L;

        UpdateEmployeeRequest requestDto = UpdateEmployeeRequest.builder()
                .firstName("Елисей")
                .lastName("Интерстелларов")
                .email("elisey909@gmail.com")
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        when(employeeService.update(eq(id), any(UpdateEmployeeRequest.class)))
                .thenThrow(new EntityNotFoundException("Employee not found with id: " + id));

        // --- WHEN & THEN ---
        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }
}
