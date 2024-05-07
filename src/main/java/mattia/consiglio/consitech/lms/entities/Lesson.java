package mattia.consiglio.consitech.lms.entities;

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
    private String title;
    private String slug;
    private String description;
    private String live_editor;
    private String video_url;
    private String video_thumbnail;
    private String content;
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
