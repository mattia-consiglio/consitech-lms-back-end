package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record NewSeoDTO(
        @NotBlank(message = "title is required")
        @Size(min = 3, message = "title must at least 3 characters long")
        String title,
        @NotBlank(message = "description is required")
        @Size(min = 3, message = "description must at least 3 characters long")
        String description,
        @NotNull(message = "ldJson is required")
        String ldJson,
        @NotNull(message = "languageId is required")
        UUID languageId
) {
}
