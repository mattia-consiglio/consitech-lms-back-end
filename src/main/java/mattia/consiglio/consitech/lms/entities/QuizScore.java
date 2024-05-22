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
@Table(name = "quiz_scores")
public class QuizScore {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.NONE)
    private UUID id;
    private int score;
    @Column(name = "answered_questions")
    private String answeredQuestions;
    @ManyToOne
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
