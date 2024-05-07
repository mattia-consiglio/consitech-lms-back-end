package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record UpdateCourseDTO(
        @NotBlank(message = "title is required")
        String title,
        @NotBlank(message = "slug is required")
        String slug,
        @NotBlank(message = "description is required")
        String description,
        UUID thumbnailId
) {
}
