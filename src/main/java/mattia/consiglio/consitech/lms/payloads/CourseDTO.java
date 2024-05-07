package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CourseDTO(
        @NotBlank(message = "title is required")
        @Size(min = 3, message = "title must at least 3 characters long")
        String title,
        @NotBlank(message = "slug is required")
        String slug,
        @NotBlank(message = "description is required")
        @Size(min = 50, max = 200, message = "description must be between 50 and 200 characters long")
        String description,
        @NotBlank(message = "enrolledStudents is required")
        @PositiveOrZero(message = "enrolledStudents must be a positive number")
        int enrolledStudents,
        @NotNull(message = "seoId is required")
        UUID seoId,
        @NotNull(message = "mainLanguageId is required")
        UUID mainLanguageId
) {
}
