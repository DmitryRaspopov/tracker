package io.teammetric.tracker.service;

import io.teammetric.tracker.dto.request.employee.CreateEmployeeRequest;
import io.teammetric.tracker.dto.request.employee.UpdateEmployeeRequest;
import io.teammetric.tracker.dto.response.employee.EmployeeResponse;
import io.teammetric.tracker.entity.Employee;
import io.teammetric.tracker.entity.Project;
import io.teammetric.tracker.exception.EntityNotFoundException;
import io.teammetric.tracker.mapper.employee.EmployeeMapper;
import io.teammetric.tracker.repository.EmployeeRepository;
import io.teammetric.tracker.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    @DisplayName("GetById: Если сотрудник найден — он должен быть возвращён в виде EmployeeResponse")
    void getById_WhenExists_ShouldReturnEmployeeResponse() {
        // --- GIVEN ---
        Long id = 1L;
        String firstName = "Владислав";

        EmployeeResponse expectedDto = EmployeeResponse.builder()
                .id(id)
                .firstName(firstName)
                .build();

        Employee existingEmployee = Employee.builder()
                .id(id)
                .firstName(firstName)
                .build();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeMapper.toResponse(existingEmployee)).thenReturn(expectedDto);

        // --- WHEN ---
        EmployeeResponse actualDto = employeeService.getById(id);

        // --- THEN ---
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("GetById: Если сотрудник не найден — должно быть выброшено исключение")
    void getById_WhenEmployeeNotFound_ShouldThrowException() {
        // --- GIVEN ---
        Long id = 99L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // --- WHEN & THEN ---
        assertThrows(EntityNotFoundException.class, () -> employeeService.getById(id));
    }

    @Test
    @DisplayName("FindAll: Если сотрудники существуют — должен вернуть список EmployeeResponse")
    void findAll_WhenEmployeesExist_ShouldReturnList() {
        // --- GIVEN ---
        Employee first = Employee.builder()
                .id(1L)
                .firstName("First")
                .build();

        Employee second = Employee.builder()
                .id(2L)
                .firstName("Second")
                .build();

        EmployeeResponse firstEmployeeResponse = EmployeeResponse.builder()
                .id(1L)
                .firstName("First")
                .build();

        EmployeeResponse secondEmployeeResponse = EmployeeResponse.builder()
                .id(2L)
                .firstName("Second")
                .build();

        List<Employee> employeeList = List.of(first, second);
        when(employeeRepository.findAll()).thenReturn(employeeList);
        when(employeeMapper.toResponse(first)).thenReturn(firstEmployeeResponse);
        when(employeeMapper.toResponse(second)).thenReturn(secondEmployeeResponse);

        // --- WHEN ---
        List<EmployeeResponse> actualList = employeeService.findAll();

        // --- THEN ---
        assertNotNull(actualList);
        assertEquals(2, actualList.size());
    }

    @Test
    @DisplayName("FindAll: Если сотрудников нет — должен вернуть пустой список")
    void findAll_WhenEmployeesNotFound_ShouldReturnEmptyList() {
        // --- GIVEN ---
        when(employeeRepository.findAll()).thenReturn(List.of());

        // --- WHEN ---
        List<EmployeeResponse> actualList = employeeService.findAll();

        // --- THEN ---
        assertNotNull(actualList);
        assertEquals(0, actualList.size());
    }

    @Test
    @DisplayName("Save: При вызове метода 1 раз должен вызваться репозиторий")
    void save_WhenCalled_ShouldCallRepository() {
        // --- GIVEN ---
        Long id = 1L;
        String firstName = "Даниил";

        CreateEmployeeRequest requestDto = CreateEmployeeRequest.builder()
                .firstName(firstName)
                .build();

        EmployeeResponse responseDto = EmployeeResponse.builder()
                .id(id)
                .firstName(firstName)
                .build();

        Employee employeeToSave = Employee.builder()
                .firstName(firstName)
                .build();

        Employee savedEmployee = Employee.builder()
                .id(id)
                .firstName(firstName)
                .build();

        when(employeeMapper.toEntity(requestDto)).thenReturn(employeeToSave);
        when(employeeRepository.save(employeeToSave)).thenReturn(savedEmployee);
        when(employeeMapper.toResponse(savedEmployee)).thenReturn(responseDto);

        // --- WHEN ---
        EmployeeResponse actualDto = employeeService.save(requestDto);

        // --- THEN ---
        assertNotNull(actualDto);
        verify(employeeRepository).save(employeeToSave);
    }

    @Test
    @DisplayName("Save: Если у CreateEmployeeRequest есть проект — новый сотрудник должен быть сохранён с этим проектом")
    void save_WhenProjectExists_ShouldSaveEmployeeWithProject() {
        // --- GIVEN ---
        Long employeeId = 1L;
        String firstName = "Мария";
        Long projectId = 22L;
        String projectName = "Happiness For Everyone";

        Project existingProject = Project.builder()
                .id(projectId)
                .name(projectName)
                .build();

        CreateEmployeeRequest requestDto = CreateEmployeeRequest.builder()
                .firstName(firstName)
                .projectId(projectId)
                .build();

        EmployeeResponse responseDto = EmployeeResponse.builder()
                .id(employeeId)
                .firstName(firstName)
                .projectId(projectId)
                .projectName(projectName)
                .build();

        Employee employeeToSave = Employee.builder()
                .id(employeeId)
                .firstName(firstName)
                .build();

        Employee savedEmployee = Employee.builder()
                .id(employeeId)
                .firstName(firstName)
                .project(existingProject)
                .build();

        when(employeeMapper.toEntity(requestDto)).thenReturn(employeeToSave);
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(employeeMapper.toResponse(savedEmployee)).thenReturn(responseDto);

        // --- WHEN ---
        EmployeeResponse actualDto = employeeService.save(requestDto);

        // --- THEN ---
        assertNotNull(actualDto);

        // Test side effect: the service should find the project and save it to the employee
        assertEquals(existingProject, employeeToSave.getProject());

        assertEquals(employeeId, actualDto.id());
        assertEquals(projectId, actualDto.projectId());
    }

    @Test
    @DisplayName("Update: При вызове метода 1 раз должен вызваться репозиторий")
    void update_WhenCalled_ShouldCallRepository() {
        // --- GIVEN ---
        Long id = 1L;
        String firstName = "Дмитрий";

        UpdateEmployeeRequest requestDto = UpdateEmployeeRequest.builder()
                .firstName(firstName)
                .build();

        EmployeeResponse responseDto = EmployeeResponse.builder()
                .id(id)
                .firstName(firstName)
                .build();

        Employee existingEmployee = Employee.builder()
                .id(id)
                .firstName(firstName)
                .build();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(existingEmployee);
        when(employeeMapper.toResponse(existingEmployee)).thenReturn(responseDto);

        // --- WHEN ---
        EmployeeResponse actualDto = employeeService.update(id, requestDto);

        // --- THEN ---
        assertNotNull(actualDto);
        verify(employeeRepository).save(existingEmployee);
    }

    @Test
    @DisplayName("Update: Если проект в UpdateEmployeeRequest отличается от проекта текущего сотрудника — проект должен быть изменён")
    void update_WhenProjectChanged_ShouldReplaceOldOne() {
        // --- GIVEN ---
        Long employeeId = 1L;
        String firstName = "Семён";

        Long oldProjectId = 33L;
        Project oldProject = Project.builder()
                .id(oldProjectId)
                .name("Quack")
                .build();

        Long newProjectId = 34L;
        String newProjectName = "Quack-quack";
        Project newProject = Project.builder()
                .id(newProjectId)
                .name(newProjectName)
                .build();

        Employee existingEmployee = Employee.builder()
                .id(employeeId)
                .firstName(firstName)
                .project(oldProject)
                .build();

        Employee updatedEmployee = Employee.builder()
                .id(employeeId)
                .firstName(firstName)
                .project(newProject)
                .build();

        UpdateEmployeeRequest requestDto = UpdateEmployeeRequest.builder()
                .firstName(firstName)
                .projectId(newProjectId)
                .build();

        EmployeeResponse responseDto = EmployeeResponse.builder()
                .id(employeeId)
                .firstName(firstName)
                .projectId(newProjectId)
                .projectName(newProjectName)
                .build();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);
        when(projectRepository.findById(newProjectId)).thenReturn(Optional.of(newProject));
        when(employeeMapper.toResponse(updatedEmployee)).thenReturn(responseDto);

        // --- WHEN ---
        EmployeeResponse actualDto = employeeService.update(employeeId, requestDto);

        // --- THEN ---
        assertNotNull(actualDto);
        assertEquals(employeeId, actualDto.id());
        assertEquals(firstName, actualDto.firstName());
        assertEquals(newProjectId, actualDto.projectId());

        // Check that the project has been replaced in the employee that we extracted from the DB
        assertEquals(newProject, existingEmployee.getProject());
    }

    @Test
    @DisplayName("Update: Если проект не менялся — поиск в БД не выполняется")
    void update_WhenProjectUnchanged_ShouldSkipProjectRepo() {
        // --- GIVEN ---
        Long employeeId = 1L;
        String firstName = "Семён";

        Long projectId = 33L;
        String projectName = "Quack";

        Project project = Project.builder()
                .id(projectId)
                .name(projectName)
                .build();

        Employee existingEmployee = Employee.builder()
                .id(employeeId)
                .firstName(firstName)
                .project(project)
                .build();

        UpdateEmployeeRequest requestDto = UpdateEmployeeRequest.builder()
                .firstName(firstName)
                .projectId(projectId)
                .build();

        EmployeeResponse responseDto = EmployeeResponse.builder()
                .id(employeeId)
                .firstName(firstName)
                .projectId(projectId)
                .projectName(projectName)
                .build();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(existingEmployee);
        when(employeeMapper.toResponse(existingEmployee)).thenReturn(responseDto);

        // --- WHEN ---
        employeeService.update(employeeId, requestDto);

        // --- THEN ---
        verify(projectRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Update: Если в UpdateEmployeeRequest нет проекта, а у сотрудника он был — проект должен быть удалён")
    void update_WhenProjectRemoved_ShouldClearField() {
        // --- GIVEN ---
        Long employeeId = 1L;
        String firstName = "Тёма";

        Project oldProject = Project.builder()
                .id(35L)
                .name("Meow")
                .build();

        Employee existingEmployee = Employee.builder()
                .id(employeeId)
                .firstName(firstName)
                .project(oldProject)
                .build();

        Employee updatedEmployee = Employee.builder()
                .id(employeeId)
                .firstName(firstName)
                .project(null)
                .build();

        UpdateEmployeeRequest requestDto = UpdateEmployeeRequest.builder()
                .firstName(firstName)
                .projectId(null)
                .build();

        EmployeeResponse responseDto = EmployeeResponse.builder()
                .id(employeeId)
                .firstName(firstName)
                .projectId(null)
                .projectName(null)
                .build();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);
        when(employeeMapper.toResponse(updatedEmployee)).thenReturn(responseDto);

        // --- WHEN ---
        EmployeeResponse actualDto = employeeService.update(employeeId, requestDto);

        // --- THEN ---
        assertNotNull(actualDto);
        assertEquals(employeeId, actualDto.id());
        assertNull(actualDto.projectId());
        assertNull(actualDto.projectName());
        assertNull(existingEmployee.getProject());
    }

    @Test
    @DisplayName("Update: Если сотрудник не найден — должно быть выброшено исключение")
    void update_WhenEmployeeNotFound_ShouldThrowException() {
        // --- GIVEN ---
        Long id = 99L;
        UpdateEmployeeRequest requestDto = UpdateEmployeeRequest.builder()
                .firstName("Happy New Year")
                .build();

        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        // --- WHEN & THEN ---
        assertThrows(EntityNotFoundException.class, () -> employeeService.update(id, requestDto));
    }

    @Test
    @DisplayName("Update: Если проект не найден — должно быть выброшено исключение")
    void update_WhenProjectNotFound_ShouldThrowException() {
        // --- GIVEN ---
        Long employeeId = 1L;
        String firstName = "Ксения";

        Long nonExistingProjectId = 99L;

        Project oldProject = Project.builder()
                .id(40L)
                .name("Grooming")
                .build();

        Employee existingEmployee = Employee.builder()
                .id(employeeId)
                .firstName(firstName)
                .project(oldProject)
                .build();

        UpdateEmployeeRequest requestDto = UpdateEmployeeRequest.builder()
                .firstName(firstName)
                .projectId(nonExistingProjectId)
                .build();

        // --- WHEN ---
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(projectRepository.findById(nonExistingProjectId)).thenReturn(Optional.empty());

        // --- THEN ---
        assertThrows(EntityNotFoundException.class, () -> employeeService.update(employeeId, requestDto));
        verify(employeeRepository, never()).save(any());
    }
}
