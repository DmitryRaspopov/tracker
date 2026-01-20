package io.teammetric.tracker.controller;

import io.teammetric.tracker.dto.EmployeeDto;
import io.teammetric.tracker.entity.Employee;
import io.teammetric.tracker.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@Valid @RequestBody EmployeeDto employeeDto) {
        Employee employee = mappingEmployeeDtoToEmployee(employeeDto);
        employeeService.save(employee);
    }

    @GetMapping("/{id}")
    public EmployeeDto getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        return mappingEmployeeToEmployeeDto(employee);
    }

    @PutMapping("/{id}")
    public EmployeeDto update(@PathVariable Long id, @Valid @RequestBody EmployeeDto employeeDto) {
        Employee requestEntity = mappingEmployeeDtoToEmployee(employeeDto);
        Employee updatedEmployee = employeeService.update(id, requestEntity);
        EmployeeDto responseDto = mappingEmployeeToEmployeeDto(updatedEmployee);
        return responseDto;
    }

    private EmployeeDto mappingEmployeeToEmployeeDto(Employee employee) {
        return EmployeeDto.builder()
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .middleName(employee.getMiddleName())
                .username(employee.getUsername())
                .email(employee.getEmail())
                .build();
    }

    private Employee mappingEmployeeDtoToEmployee(EmployeeDto employeeDto) {
        return Employee.builder()
                .firstName(employeeDto.getFirstName())
                .lastName(employeeDto.getLastName())
                .middleName(employeeDto.getMiddleName())
                .username(employeeDto.getUsername())
                .email(employeeDto.getEmail())
                .build();
    }
}
