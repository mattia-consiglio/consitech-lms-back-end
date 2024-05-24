package mattia.consiglio.consitech.lms.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.UUID;

public class UUIDValidator implements ConstraintValidator<ValidUUID, String> {

    @Override
    public void initialize(ValidUUID constraintAnnotation) {
    }

    @Override
    public boolean isValid(String uuid, ConstraintValidatorContext context) {
        if (uuid == null) {
            return false;
        }
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
