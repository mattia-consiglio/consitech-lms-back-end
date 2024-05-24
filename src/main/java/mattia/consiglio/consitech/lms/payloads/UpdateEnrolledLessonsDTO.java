package mattia.consiglio.consitech.lms.payloads;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDate;

public record UpdateEnrolledLessonsDTO(
        LocalDate endDate,
        @NotNull(message = "videoWatchTimePercentage is required")
        @PositiveOrZero(message = "videoWatchTimePercentage must be positive or zero")
        double videoWatchTimePercentage,
        @NotNull(message = "quizDone is required")
        boolean quizDone
) {
}
