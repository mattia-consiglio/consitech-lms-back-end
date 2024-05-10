package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "courses")
public class Course extends AbstractContent {
    private int enrolledStudents;
    @OneToMany(mappedBy = "course")
    private List<Lesson> lessons;
}
