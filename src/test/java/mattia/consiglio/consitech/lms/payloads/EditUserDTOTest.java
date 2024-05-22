package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import mattia.consiglio.consitech.lms.config.ValidationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class EditUserDTOTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ValidationConfig.class);
        validator = context.getBean(Validator.class);
    }

    @Test
    void testValidConstructorArgs() {
        EditUserDTO dto = new EditUserDTO("validUsername", "validPassword123", "valid@email.com", "ADMIN");
        Set<ConstraintViolation<EditUserDTO>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "ab", "  "})
    void testInvalidUsername(String username) {
        EditUserDTO dto = new EditUserDTO(username, "validPassword123", "valid@email.com", "ADMIN");
        Set<ConstraintViolation<EditUserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        assertTrue(errorMessage.contains("username is required") || errorMessage.contains("username must at least 3 characters long"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "shortPassword", "            "})
    void testInvalidPassword(String password) {
        EditUserDTO dto = new EditUserDTO("validUsername", password, "valid@email.com", "ADMIN");
        Set<ConstraintViolation<EditUserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        assertTrue(errorMessage.contains("password is required") || errorMessage.contains("password must at least 15 characters long"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "invalid_email", "  "})
    void testInvalidEmail(String email) {
        EditUserDTO dto = new EditUserDTO("validUsername", "validPassword123", email, "ADMIN");
        Set<ConstraintViolation<EditUserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        assertTrue(errorMessage.contains("email is required") || errorMessage.contains("email format is invalid"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "  ", "INVALID_ROLE"})
    void testInvalidRole(String role) {
        EditUserDTO dto = new EditUserDTO("validUsername", "validPassword123", "valid@email.com", role);
        Set<ConstraintViolation<EditUserDTO>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        String errorMessage = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        assertTrue(errorMessage.contains("role is required") || errorMessage.contains("Value must be one of the following:"));
    }

    @Test
    void testImmutability() {
        EditUserDTO dto = new EditUserDTO("validUsername", "validPassword123", "valid@email.com", "ADMIN");
        EditUserDTO dtoClone = new EditUserDTO(dto.username(), dto.password(), dto.email(), dto.role());

        // Attempt to modify the fields of the cloned instance
        dtoClone.username().concat("_modified");
        dtoClone.password().concat("_modified");
        dtoClone.email().concat("_modified");
        dtoClone.role().concat("_modified");

        // Assert that the original instance is still equal to the cloned instance
        assertEquals(dto, dtoClone);
    }

    @Test
    void testEqualsAndHashCode() {
        EditUserDTO dto1 = new EditUserDTO("validUsername", "validPassword123", "valid@email.com", "ADMIN");
        EditUserDTO dto2 = new EditUserDTO("validUsername", "validPassword123", "valid@email.com", "ADMIN");
        EditUserDTO dto3 = new EditUserDTO("differentUsername", "validPassword123", "valid@email.com", "ADMIN");

        assertEquals(dto1, dto2);
        assertNotEquals(dto1, dto3);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1.hashCode(), dto3.hashCode());
    }
}