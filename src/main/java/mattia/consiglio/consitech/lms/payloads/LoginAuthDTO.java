package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotBlank;

public record LoginAuthDTO(
        @NotBlank(message = "Username or email is required")
        String usernameOrEmail,
        @NotBlank(message = "Password is required")
        String password) {
}
