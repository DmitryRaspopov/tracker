package io.teammetric.tracker.dto.response.employee;

import lombok.Builder;

@Builder
public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String middleName,
        String username,
        String email,
        Long projectId,
        String projectName
) {
}