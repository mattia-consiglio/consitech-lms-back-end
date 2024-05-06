package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "quizzes")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.NONE)
    private UUID id;
    private String content;
    @Enumerated(value = EnumType.STRING)
    private QuizType type;
    @ManyToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;
    @ManyToOne
    @JoinColumn(name = "main_language_id")
    private Language mainLanguage;

    public Quiz(String content, QuizType type, Lesson lesson, Language mainLanguage) {
        this.content = content;
        this.type = type;
        this.lesson = lesson;
        this.mainLanguage = mainLanguage;
    }
}
