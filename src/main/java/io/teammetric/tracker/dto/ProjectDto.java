package io.teammetric.tracker.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDto {
    private Long id;

    @NotBlank(message = "Project name cannot be empty")
    private String name;

    private String description;

    @Builder.Default
    private List<EmployeeDto> employees = new ArrayList<>();
}
