package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mattia.consiglio.consitech.lms.entities.enums.PublishStatus;
import mattia.consiglio.consitech.lms.validations.ValidUUID;
import mattia.consiglio.consitech.lms.validations.ValueOfEnum;

import java.util.UUID;

public record UpdateLessonDTO(
        @NotBlank(message = "title is required")
        @Size(min = 3, max = 100, message = "title must be between 3 and 100 characters")
        String title,
        @NotBlank(message = "slug is required")
        @Size(min = 3, max = 100, message = "slug must be between 3 and 100 characters")
        String slug,
        @NotBlank(message = "description is required")
        @Size(min = 20, max = 100, message = "description must be between 20 and 100 characters")
        String description,
        @NotBlank(message = "publishStatus is required")
        @ValueOfEnum(enumClass = PublishStatus.class)
        String publishStatus,
        UUID thumbnailId,
        String liveEditor,
        @ValidUUID
        String videoId,
        String content,
        @NotNull(message = "courseId is required")
        UUID courseId
) {
}
