package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mattia.consiglio.consitech.lms.utils.View;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "lessons")

public class Lesson extends AbstractContent {
    @Column(columnDefinition = "TEXT")
    @JsonView(View.Internal.class)
    private String liveEditor;

    @ManyToOne
    @JoinColumn(name = "video_id")
    @JsonView(View.Internal.class)
    private MediaVideo video;

    @Column(columnDefinition = "TEXT")
    @JsonView(View.Internal.class)
    private String content;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonView(View.Internal.class)
    private Course course;
}
