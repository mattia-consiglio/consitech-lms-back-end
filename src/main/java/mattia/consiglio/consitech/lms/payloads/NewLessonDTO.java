package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record NewLessonDTO(
        @NotBlank(message = "title is required")
        @Size(min = 3, max = 100, message = "title must be between 3 and 100 characters")
        String title,
        @NotBlank(message = "slug is required")
        @Size(min = 3, max = 100, message = "slug must be between 3 and 100 characters")
        String slug,
        @NotBlank(message = "description is required")
        @Size(min = 20, max = 100, message = "description must be between 20 and 100 characters")
        String description,
        UUID thumbnailId,
        @NotNull(message = "mainLanguageId is required")
        UUID mainLanguageId,
        String liveEditor,
        String videoUrl,
        String content,
        @NotNull(message = "courseId is required")
        UUID courseId
) {
}
