package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserFullUpdateDTO(@NotBlank(message = "username is required")
                            @Size(min = 3, message = "username must at least 3 characters long")
                            String username,
                                @NotBlank(message = "oldPassword is required")
                            @Size(min = 15, max = 50, message = "password must be between 15 and 50 characters long")
                            @Pattern(regexp = "^(?=(?:.*[A-Z]){2,})(?=(?:.*[a-z]){2,})(?=(?:.*\\d){2,})(?=(?:.*[!@#$%^&*()\\-_=+{};:,<.>]){2,})(?!.*(.)\\1{2})([A-Za-z0-9!@#$%^&*()\\-_=+{};:,<.>]{15,50})$", message = "password must contain at least 2 uppercase letters, 2 lowercase letters, 2 digits, 2 special characters and must not contain repeated characters more than 2 times")
                            String oldPassword,
                                @NotBlank(message = "newPassword is required")
                            @Size(min = 15, max = 50, message = "password must be between 15 and 50 characters long")
                            @Pattern(regexp = "^(?=(?:.*[A-Z]){2,})(?=(?:.*[a-z]){2,})(?=(?:.*\\d){2,})(?=(?:.*[!@#$%^&*()\\-_=+{};:,<.>]){2,})(?!.*(.)\\1{2})([A-Za-z0-9!@#$%^&*()\\-_=+{};:,<.>]{15,50})$", message = "password must contain at least 2 uppercase letters, 2 lowercase letters, 2 digits, 2 special characters and must not contain repeated characters more than 2 times")
                            String newPassword,
                                @NotBlank(message = "email is required")
                            @Email(message = "email format is invalid")
                            String email) {
}
