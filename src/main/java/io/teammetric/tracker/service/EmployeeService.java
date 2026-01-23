package io.teammetric.tracker.service;

import io.teammetric.tracker.entity.Employee;
import io.teammetric.tracker.exception.EntityNotFoundException;
import io.teammetric.tracker.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;

    @Transactional
    public Employee save(Employee employee) {
        return employeeRepository.save(employee);
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee getById(Long id) {
        return employeeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Employee not found with id: " + id)
        );
    }

    @Transactional
    public Employee update(Long id, Employee employeeDetails) {
        Employee employee = getById(id);

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setMiddleName(employeeDetails.getMiddleName());
        employee.setUsername(employeeDetails.getUsername());
        employee.setEmail(employeeDetails.getEmail());

        return employeeRepository.save(employee);
    }
}
