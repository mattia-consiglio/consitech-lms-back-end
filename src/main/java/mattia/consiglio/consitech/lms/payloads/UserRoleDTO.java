package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;
import mattia.consiglio.consitech.lms.entities.enums.UserRole;
import mattia.consiglio.consitech.lms.validations.ValueOfEnum;

public record UserRoleDTO(@NotBlank(message = "role is required")
                          @ValueOfEnum(enumClass = UserRole.class)
                          String role) {
}
