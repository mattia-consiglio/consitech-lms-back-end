package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;
import mattia.consiglio.consitech.lms.entities.MediaType;
import mattia.consiglio.consitech.lms.validations.ValueOfEnum;

public record MediaDTO(
        @NotBlank(message = "mediaType is required")
        @ValueOfEnum(enumClass = MediaType.class)
        String mediaType
) {
}
