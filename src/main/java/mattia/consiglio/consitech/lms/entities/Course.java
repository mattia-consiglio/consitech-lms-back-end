package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "courses")
public class Course extends AbstractContent {
    private String title;
    private String slug;
    private String description;
    private int enrolledStudents;
}
