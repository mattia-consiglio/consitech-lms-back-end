package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LanguageDTO(
        @NotBlank(message = "code is required")
        @Size(min = 2, max = 2, message = "code must be 2 characters long")
        String code,
        @NotBlank(message = "language is required")
        String language
) {
}
