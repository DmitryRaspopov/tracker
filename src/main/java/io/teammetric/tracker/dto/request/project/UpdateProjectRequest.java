package io.teammetric.tracker.dto.request.project;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateProjectRequest(
        @NotBlank(message = "Project name cannot be empty")
        String name,

        String description
) {
}