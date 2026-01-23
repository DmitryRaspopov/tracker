package io.teammetric.tracker.service;

import io.teammetric.tracker.entity.Employee;
import io.teammetric.tracker.exception.EntityNotFoundException;
import io.teammetric.tracker.repository.EmployeeRepository;
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

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    void getById_ShouldReturnEmployee_WhenExists() {
        Long employeeId = 1L;
        Employee mockEmployee = Employee.builder()
                .id(employeeId)
                .firstName("Владислав")
                .build();

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(mockEmployee));

        Employee result = employeeService.getById(employeeId);

        assertNotNull(result);
        assertEquals(employeeId, result.getId());
        assertEquals("Владислав", result.getFirstName());
    }

    @Test
    void getById_ShouldThrowException_WhenEmployeeNotFound() {
        Long id = 99L;

        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            employeeService.getById(id);
        });
    }

    @Test
    void save_ShouldCallRepository_WhenCalled() {
        Employee mockEmployee = Employee.builder()
                .id(1L)
                .firstName("Даниил")
                .build();

        when(employeeRepository.save(mockEmployee)).thenReturn(mockEmployee);

        Employee result = employeeService.save(mockEmployee);
        assertNotNull(result);

        verify(employeeRepository, times(1)).save(mockEmployee);
    }
}
