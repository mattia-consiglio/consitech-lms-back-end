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
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.NONE)
    private UUID id;
    private String content;
    private double second; // second in which the user placed the note
    @ManyToOne
    @JoinColumn(name = "enrolled_lesson_id")
    private EnrolledLesson enrolledLesson;
}
