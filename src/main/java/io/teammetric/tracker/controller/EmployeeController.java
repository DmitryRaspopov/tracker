package io.teammetric.tracker.controller;

import io.teammetric.tracker.dto.EmployeeDto;
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

    @GetMapping("/{id}")
    public EmployeeDto getById(@PathVariable("id") Long id) {
        return employeeService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeDto create(@Valid @RequestBody EmployeeDto employeeDto) {
        return employeeService.save(employeeDto);
    }

    @PutMapping("/{id}")
    public EmployeeDto update(@PathVariable("id") Long id, @Valid @RequestBody EmployeeDto employeeDto) {
        return employeeService.update(id, employeeDto);
    }
}
