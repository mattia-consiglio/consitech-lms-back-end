package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mattia.consiglio.consitech.lms.validations.ValueOfEnum;

import java.util.UUID;

public record UpdateCourseDTO(
        @NotBlank(message = "title is required")
        @Size(min = 2, max = 100, message = "title must be between 3 and 100 characters")
        String title,
        @NotBlank(message = "slug is required")
        @Size(min = 2, max = 100, message = "slug must be between 3 and 100 characters")
        String slug,
        @NotBlank(message = "description is required")
        @Size(min = 20, max = 150, message = "description must be between 20 and 100 characters")
        String description,
        @NotBlank(message = "publishStatus is required")
        @ValueOfEnum(enumClass = mattia.consiglio.consitech.lms.entities.PublishStatus.class)
        String publishStatus,
        UUID thumbnailId
) {
}
