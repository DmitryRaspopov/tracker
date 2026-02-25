package io.teammetric.tracker.mapper.project;

import io.teammetric.tracker.dto.request.project.CreateProjectRequest;
import io.teammetric.tracker.dto.response.employee.EmployeeResponse;
import io.teammetric.tracker.dto.response.project.ProjectResponse;
import io.teammetric.tracker.entity.Project;
import io.teammetric.tracker.mapper.employee.EmployeeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProjectMapper {
    private final EmployeeMapper employeeMapper;

    public ProjectResponse toResponse(Project project) {
        List<EmployeeResponse> employeeResponseList = project.getEmployees().stream()
                .map(employeeMapper::toResponse)
                .toList();

        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .employees(employeeResponseList)
                .build();
    }

    public Project toEntity(CreateProjectRequest projectRequest) {

        return Project.builder()
                .name(projectRequest.name())
                .description(projectRequest.description())
                .build();
    }
}
