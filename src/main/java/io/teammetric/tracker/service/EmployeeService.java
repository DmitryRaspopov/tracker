package io.teammetric.tracker.service;

import io.teammetric.tracker.dto.EmployeeDto;
import io.teammetric.tracker.entity.Employee;
import io.teammetric.tracker.entity.Project;
import io.teammetric.tracker.exception.EntityNotFoundException;
import io.teammetric.tracker.mapper.EmployeeMapper;
import io.teammetric.tracker.repository.EmployeeRepository;
import io.teammetric.tracker.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeDto getById(Long id) {
        Employee employee = getEmployeeById(id);

        return employeeMapper.toDto(employee);
    }

    public List<EmployeeDto> findAll() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeDto> employeeDtoList = new ArrayList<>(employees.size());

        for (Employee employee : employees) {
            employeeDtoList.add(employeeMapper.toDto(employee));
        }

        return employeeDtoList;
    }

    @Transactional
    public EmployeeDto save(EmployeeDto employeeDto) {
        boolean dtoHasProject = employeeDto.getProjectId() != null;
        Employee employee = employeeMapper.toEmployee(employeeDto);

        if (dtoHasProject) {
            Long projectId = employeeDto.getProjectId();
            Project project = getProjectById(projectId);
            employee.setProject(project);
        }

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional
    public EmployeeDto update(Long id, EmployeeDto employeeDetails) {
        Employee employee = getEmployeeById(id);

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setMiddleName(employeeDetails.getMiddleName());
        employee.setUsername(employeeDetails.getUsername());
        employee.setEmail(employeeDetails.getEmail());

        Long currentProjectId = employee.getProject() == null ? null : employee.getProject().getId();
        Long newProjectId = employeeDetails.getProjectId();

        if (!Objects.equals(currentProjectId, newProjectId)) {
            if (newProjectId != null) {
                Project newProject = getProjectById(newProjectId);
                employee.setProject(newProject);
            } else {
                employee.setProject(null);
            }
        }

        Employee updatedEmployee = employeeRepository.save(employee);

        return employeeMapper.toDto(updatedEmployee);
    }

    private Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Employee not found with id: " + id)
        );
    }

    private Project getProjectById(Long id) {
        return projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Project not found with id: " + id)
        );
    }
}