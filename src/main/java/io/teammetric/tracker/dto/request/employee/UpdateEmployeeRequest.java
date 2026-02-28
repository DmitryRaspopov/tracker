package io.teammetric.tracker.dto.request.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateEmployeeRequest(
        @NotBlank(message = "Firstname cannot be empty")
        String firstName,

        @NotBlank(message = "Lastname cannot be empty")
        String lastName,

        String middleName,

        @Email(message = "Email is not valid")
        String email,

        Long projectId
) {
}
