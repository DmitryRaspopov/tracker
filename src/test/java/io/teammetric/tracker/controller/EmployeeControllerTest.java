package io.teammetric.tracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.teammetric.tracker.dto.EmployeeDto;
import io.teammetric.tracker.exception.EntityNotFoundException;
import io.teammetric.tracker.service.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


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
    void getById_WhenExists_ShouldReturnStatusOk() throws Exception {
        // --- GIVEN ---
        Long id = 1L;
        String firstName = "Владислав";

        EmployeeDto employeeDto = EmployeeDto.builder()
                .id(id)
                .firstName(firstName)
                .build();

        when(employeeService.getById(id)).thenReturn(employeeDto);

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
    @DisplayName("Create: При создании сотрудника с валидными данными — должен вернуться статус 201 и JSON с данными")
    void create_WhenValidInput_ShouldReturnStatusCreated() throws Exception {
        // --- GIVEN ---
        Long id = 1L;
        String firstName = "Роман";
        String lastName = "Лежепёков";
        String username = "Roman111";
        String email = "roman111@gmail.com";

        EmployeeDto requestDto = EmployeeDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .build();

        EmployeeDto responseDto = EmployeeDto.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        when(employeeService.save(any(EmployeeDto.class))).thenReturn(responseDto);

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
        Long id = 2L;
        String firstName = "";
        String lastName = "Лежепёков";
        String username = "Roman111";
        String email = "roman111@gmail.com";

        EmployeeDto requestDto = EmployeeDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
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
        String username = "Danik333";
        String email = "danik333@gmail.com";

        EmployeeDto requestDto = EmployeeDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .build();

        EmployeeDto responseDto = EmployeeDto.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        when(employeeService.update(eq(id), any(EmployeeDto.class))).thenReturn(responseDto);

        // --- WHEN & THEN ---
        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.email").value(email));
    }

    @Test
    @DisplayName("Update: При обновлении сотрудника с невалидными данными — должен вернуться статус 400")
    void update_WhenInvalidInput_ShouldReturnBadRequest() throws Exception {
        Long id = 5L;
        String firstName = "Дмитрий";
        String lastName = "";
        String username = "Dmitry878";
        String email = "rdmitry@gmail.com";

        EmployeeDto requestDto = EmployeeDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/api/employees/{id}", id)
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
        String firstName = "Елисей";
        String lastName = "Интерстелларов";
        String username = "Elisey909";
        String email = "elisey909@gmail.com";

        EmployeeDto requestDto = EmployeeDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .build();

        String requestJson = jacksonObjectMapper.writeValueAsString(requestDto);

        when(employeeService.update(eq(id), any(EmployeeDto.class)))
                .thenThrow(new EntityNotFoundException("Entity not found with id: " + id));

        // --- WHEN & THEN ---
        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }
}
