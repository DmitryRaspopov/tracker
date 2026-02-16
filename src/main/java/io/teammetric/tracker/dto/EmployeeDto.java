package io.teammetric.tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDto {
    private Long id;

    @NotBlank(message = "Firstname cannot be empty")
    private String firstName;

    @NotBlank(message = "Lastname cannot be empty")
    private String lastName;

    private String middleName;

    @NotBlank(message = "Username cannot be empty")
    private String username;

    @Email(message = "Email is not valid")
    private String email;

    private Long projectId;

    private String projectName;
}
