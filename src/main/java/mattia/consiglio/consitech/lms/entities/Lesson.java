package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lessons")
public class Lesson extends AbstractContent {
    private String liveEditor;
    private String videoId;
    private String videoThumbnail;
    private String content;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
