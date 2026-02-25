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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeResponse getById(Long id) {
        Employee employee = getEmployeeById(id);

        return employeeMapper.toResponse(employee);
    }

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll().stream()
                .map(employeeMapper::toResponse)
                .toList();
    }

    @Transactional
    public EmployeeResponse save(CreateEmployeeRequest employeeRequest) {
        boolean employeeRequestHasProject = employeeRequest.projectId() != null;
        Employee employee = employeeMapper.toEntity(employeeRequest);

        if (employeeRequestHasProject) {
            Long projectId = employeeRequest.projectId();
            Project project = getProjectById(projectId);
            employee.setProject(project);
        }

        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toResponse(savedEmployee);
    }

    @Transactional
    public EmployeeResponse update(Long id, UpdateEmployeeRequest employeeRequest) {
        Employee employeeToSave = getEmployeeById(id);

        employeeToSave.setFirstName(employeeRequest.firstName());
        employeeToSave.setLastName(employeeRequest.lastName());
        employeeToSave.setMiddleName(employeeRequest.middleName());
        employeeToSave.setEmail(employeeRequest.email());

        Long currentProjectId = employeeToSave.getProject() == null ? null : employeeToSave.getProject().getId();
        Long newProjectId = employeeRequest.projectId();

        if (!Objects.equals(currentProjectId, newProjectId)) {
            if (newProjectId != null) {
                Project newProject = getProjectById(newProjectId);
                employeeToSave.setProject(newProject);
            } else {
                employeeToSave.setProject(null);
            }
        }

        Employee updatedEmployee = employeeRepository.save(employeeToSave);

        return employeeMapper.toResponse(updatedEmployee);
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