package io.teammetric.tracker.dto.response.project;

import io.teammetric.tracker.dto.response.employee.EmployeeResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record ProjectResponse(
        Long id,
        String name,
        String description,
        List<EmployeeResponse> employees
) {
    public ProjectResponse {
        if (employees == null) {
            employees = List.of();
        }
    }
}
