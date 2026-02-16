package io.teammetric.tracker.mapper;

import io.teammetric.tracker.dto.EmployeeDto;
import io.teammetric.tracker.dto.ProjectDto;
import io.teammetric.tracker.entity.Employee;
import io.teammetric.tracker.entity.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectMapper {
    private final EmployeeMapper employeeMapper;

    public ProjectDto toDto(Project project) {
        List<Employee> employees = project.getEmployees();
        List<EmployeeDto> employeeDtoList = new ArrayList<>(employees.size());

        for(Employee employee: employees) {
            employeeDtoList.add(employeeMapper.toDto(employee));
        }

        return ProjectDto.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .employees(employeeDtoList)
                .build();
    }

    public Project toProject(ProjectDto projectDto) {
        List<EmployeeDto> employeeDtoList = projectDto.getEmployees();
        List<Employee> employees = new ArrayList<>(employeeDtoList.size());

        for (EmployeeDto employeeDto: employeeDtoList) {
            employees.add(employeeMapper.toEmployee(employeeDto));
        }

        return Project.builder()
                .id(projectDto.getId())
                .name(projectDto.getName())
                .description(projectDto.getDescription())
                .employees(employees)
                .build();
    }
}
