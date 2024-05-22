package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "enrolled_lessons")
public class EnrolledLesson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.NONE)
    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    private double videoWatchTimePercentage;
    private boolean quizDone;
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
