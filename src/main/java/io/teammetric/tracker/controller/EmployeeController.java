package io.teammetric.tracker.controller;

import io.teammetric.tracker.dto.request.employee.CreateEmployeeRequest;
import io.teammetric.tracker.dto.request.employee.UpdateEmployeeRequest;
import io.teammetric.tracker.dto.response.employee.EmployeeResponse;
import io.teammetric.tracker.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping("/{id}")
    public EmployeeResponse getById(@PathVariable("id") Long id) {
        return employeeService.getById(id);
    }

    @GetMapping
    public List<EmployeeResponse> findAll() {
        return employeeService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponse create(@Valid @RequestBody CreateEmployeeRequest employeeRequest) {
        return employeeService.save(employeeRequest);
    }

    @PutMapping("/{id}")
    public EmployeeResponse update(@PathVariable("id") Long id, @Valid @RequestBody UpdateEmployeeRequest employeeRequest) {
        return employeeService.update(id, employeeRequest);
    }
}
