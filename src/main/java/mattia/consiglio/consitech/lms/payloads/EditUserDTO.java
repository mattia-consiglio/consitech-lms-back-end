package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import mattia.consiglio.consitech.lms.entities.UserRole;
import mattia.consiglio.consitech.lms.validations.ValueOfEnum;

public record EditUserDTO(
        @NotBlank(message = "username is required")
        @Size(min = 3, message = "username must at least 3 characters long")
        String username,
        @NotBlank(message = "password is required")
        @Size(min = 15, message = "password must at least 15 characters long")
        String password,
        @NotBlank(message = "email is required")
        @Email(message = "email format is invalid")
        String email,
        @NotBlank(message = "role is required")
        @ValueOfEnum(enumClass = UserRole.class)
        String role
) {
}
