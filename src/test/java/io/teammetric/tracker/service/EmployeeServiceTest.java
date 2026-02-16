package io.teammetric.tracker.service;

import io.teammetric.tracker.dto.EmployeeDto;
import io.teammetric.tracker.entity.Employee;
import io.teammetric.tracker.entity.Project;
import io.teammetric.tracker.exception.EntityNotFoundException;
import io.teammetric.tracker.mapper.EmployeeMapper;
import io.teammetric.tracker.repository.EmployeeRepository;
import io.teammetric.tracker.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    @DisplayName("GetById: Если сотрудник есть в БД — он должен быть возвращён в виде DTO")
    void getById_WhenExists_ShouldReturnEmployeeDto() {
        // --- GIVEN ---
        Long id = 1L;
        String firstName = "Владислав";

        EmployeeDto expectedDto = EmployeeDto.builder()
                .id(id)
                .firstName(firstName)
                .build();

        Employee existingEmployee = Employee.builder()
                .id(id)
                .firstName(firstName)
                .build();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeMapper.toDto(existingEmployee)).thenReturn(expectedDto);

        // --- WHEN ---
        EmployeeDto actualDto = employeeService.getById(id);

        // --- THEN ---
        assertNotNull(actualDto);
        assertEquals(expectedDto, actualDto);
    }

    @Test
    @DisplayName("GetById: Если сотрудник не найден — должно быть выброшено исключение")
    void getById_WhenEmployeeNotFound_ShouldThrowException() {
        Long id = 99L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employeeService.getById(id));
    }

    @Test
    @DisplayName("Save: При вызове метода 1 раз должен вызваться репозиторий")
    void save_WhenCalled_ShouldCallRepository() {
        // --- GIVEN ---
        Long id = 1L;
        String firstName = "Даниил";

        EmployeeDto inputDto = EmployeeDto.builder()
                .id(id)
                .firstName(firstName)
                .build();

        Employee employeeToSave = Employee.builder()
                .id((id))
                .firstName(firstName)
                .build();

        Employee savedEmployee = Employee.builder()
                .id(id)
                .firstName(firstName)
                .build();

        when(employeeRepository.save(employeeToSave)).thenReturn(savedEmployee);
        when(employeeMapper.toEmployee(inputDto)).thenReturn(employeeToSave);
        when(employeeMapper.toDto(savedEmployee)).thenReturn(inputDto);

        // --- WHEN ---
        EmployeeDto actualDto = employeeService.save(inputDto);

        // --- THEN ---
        assertNotNull(actualDto);
        verify(employeeRepository).save(employeeToSave);
    }

    //TODO: продолжить переименовывать переменные с этого метода ↓
    @Test
    @DisplayName("Save: Если у DTO есть проект — новый сотрудник должен быть сохранён с этим проектом")
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

        EmployeeDto inputDto = EmployeeDto.builder()
                .id(employeeId)
                .firstName(firstName)
                .projectId(projectId)
                .projectName(projectName)
                .build();

        Employee mappedEmployee = Employee.builder()
                .id(employeeId)
                .firstName(firstName)
                .build();

        Employee savedEmployee = Employee.builder()
                .id(employeeId)
                .firstName(firstName)
                .project(existingProject)
                .build();

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(existingProject));
        when(employeeMapper.toEmployee(inputDto)).thenReturn(mappedEmployee);
        when(employeeMapper.toDto(savedEmployee)).thenReturn(inputDto);

        // --- WHEN ---
        EmployeeDto actualDto = employeeService.save(inputDto);

        // --- THEN ---
        assertNotNull(actualDto);

        // Test side effect: the service should find the project and save it to the employee
        assertEquals(existingProject, mappedEmployee.getProject());

        assertEquals(employeeId, actualDto.getId());
        assertEquals(projectId, actualDto.getProjectId());
    }

    @Test
    @DisplayName("Update: При вызове метода 1 раз должен вызваться репозиторий")
    void update_WhenCalled_ShouldCallRepository() {
        // --- GIVEN ---
        Long id = 1L;
        String firstName = "Дмитрий";

        EmployeeDto inputDto = EmployeeDto.builder()
                .id(id)
                .firstName(firstName)
                .build();

        Employee existingEmployee = Employee.builder()
                .id(id)
                .firstName(firstName)
                .build();

        when(employeeRepository.findById(id)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(existingEmployee);
        when(employeeMapper.toDto(existingEmployee)).thenReturn(inputDto);

        // --- WHEN ---
        EmployeeDto actualDto = employeeService.update(id, inputDto);

        // --- THEN ---
        assertNotNull(actualDto);
        verify(employeeRepository).save(existingEmployee);
    }

    @Test
    @DisplayName("Update: Если проект в DTO отличается от проекта текущего сотрудника — проект должен быть изменён")
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

        EmployeeDto inputDto = EmployeeDto.builder()
                .id(employeeId)
                .firstName(firstName)
                .projectId(newProjectId)
                .projectName(newProjectName)
                .build();

        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(projectRepository.findById(newProjectId)).thenReturn(Optional.of(newProject));
        when(employeeMapper.toDto(updatedEmployee)).thenReturn(inputDto);

        // --- WHEN ---
        EmployeeDto actualDto = employeeService.update(employeeId, inputDto);

        // --- THEN ---
        assertNotNull(actualDto);
        assertEquals(employeeId, actualDto.getId());
        assertEquals(firstName, actualDto.getFirstName());
        assertEquals(newProjectId, actualDto.getProjectId());

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

        EmployeeDto inputDto = EmployeeDto.builder()
                .id(employeeId)
                .firstName(firstName)
                .projectId(projectId)
                .projectName(projectName)
                .build();

        when(employeeRepository.save(existingEmployee)).thenReturn(existingEmployee);
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeMapper.toDto(existingEmployee)).thenReturn(inputDto);

        // --- WHEN ---
        employeeService.update(employeeId, inputDto);

        // --- THEN ---
        verify(projectRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("Update: Если в DTO нет проекта, а у сотрудника он был — проект должен быть удалён")
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

        EmployeeDto inputDto = EmployeeDto.builder()
                .id(employeeId)
                .firstName(firstName)
                .projectId(null)
                .projectName(null)
                .build();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);
        when(employeeMapper.toDto(updatedEmployee)).thenReturn(inputDto);

        // --- WHEN ---
        EmployeeDto actualDto = employeeService.update(employeeId, inputDto);

        // --- THEN ---
        assertNotNull(actualDto);
        assertEquals(employeeId, actualDto.getId());
        assertNull(actualDto.getProjectId());
        assertNull(actualDto.getProjectName());
        assertNull(existingEmployee.getProject());
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

        EmployeeDto inputDto = EmployeeDto.builder()
                .id(employeeId)
                .firstName(firstName)
                .projectId(nonExistingProjectId)
                .projectName(null)
                .build();

        // --- WHEN ---
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(projectRepository.findById(nonExistingProjectId)).thenReturn(Optional.empty());

        // --- THEN ---
        assertThrows(EntityNotFoundException.class, () -> employeeService.update(employeeId, inputDto));
        verify(employeeRepository, never()).save(any());
    }
}
