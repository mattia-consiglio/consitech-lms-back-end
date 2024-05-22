package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;

public record LoginAuthDTO(
        @NotBlank(message = "usernameOrEmail is required")
        String usernameOrEmail,
        @NotBlank(message = "password is required")
        String password) {
}
