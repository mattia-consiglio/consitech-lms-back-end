package mattia.consiglio.consitech.lms.payloads;

import java.time.LocalDateTime;

public record ErrorDTO(String message, LocalDateTime timestamp) {
}
