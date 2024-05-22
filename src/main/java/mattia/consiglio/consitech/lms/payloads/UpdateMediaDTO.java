package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotNull;

public record UpdateMediaDTO(
        @NotNull(message = "alt is required")
        String alt
) {
}
