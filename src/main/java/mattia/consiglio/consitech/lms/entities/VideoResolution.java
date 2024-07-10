package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "video_resolutions")
public class VideoResolution {
    @Id
    private String name;
    private int width;
    private int height;
    @JsonIgnore
    private int crf;
}
