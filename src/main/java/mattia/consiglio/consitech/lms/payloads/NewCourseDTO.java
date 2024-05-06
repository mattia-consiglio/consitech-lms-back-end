package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record NewCourseDTO(
        @NotBlank(message = "title is required")
        String title,
        @NotBlank(message = "slug is required")
        String slug,
        @NotBlank(message = "description is required")
        String description,
        @NotNull(message = "languageId is required")
        UUID languageId
) {
}
