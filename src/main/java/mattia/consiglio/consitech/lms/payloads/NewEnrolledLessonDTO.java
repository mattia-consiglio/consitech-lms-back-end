package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;
import mattia.consiglio.consitech.lms.validations.ValidUUID;

public record NewEnrolledLessonDTO(
        @NotBlank(message = "lessonId is required")
        @ValidUUID(message = "lessonId must be a valid UUID")
        String lessonId
) {
}
