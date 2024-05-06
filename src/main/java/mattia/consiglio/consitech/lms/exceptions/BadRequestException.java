package mattia.consiglio.consitech.lms.exceptions;

import lombok.Getter;
import org.springframework.validation.ObjectError;

import java.util.List;

@Getter
public class BadRequestException extends RuntimeException {
    private List<ObjectError> errors;

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, List<ObjectError> errors) {
        super(message);
        this.errors = errors;
    }
}
