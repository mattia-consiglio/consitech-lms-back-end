package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPartialUpdateDTO(@NotBlank(message = "username is required")
                                   @Size(min = 3, message = "username must at least 3 characters long")
                                   String username,
                                   @NotBlank(message = "email is required")
                                   @Email(message = "email format is invalid")
                                   String email) {
}
