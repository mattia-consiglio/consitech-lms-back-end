package mattia.consiglio.consitech.lms.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String type, UUID id) {
        super(type + " not found with id " + id);
    }

    public ResourceNotFoundException(String type, String notFoundWith) {
        super(type + " not found with " + notFoundWith);
    }

    public ResourceNotFoundException(String type, String searchTerm, String notFoundWith) {
        super(type + " not found with " + searchTerm + " " + notFoundWith);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
