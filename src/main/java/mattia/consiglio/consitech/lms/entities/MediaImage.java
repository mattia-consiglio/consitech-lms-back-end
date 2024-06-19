package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mattia.consiglio.consitech.lms.utils.View;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "media_images")
public class MediaImage extends Media {
    @JsonView(View.Public.class)
    private int width;
    @JsonView(View.Public.class)
    private int height;
    @JsonIgnore
    private String cloudinaryPublicId;
    @JsonView(View.Public.class)
    private String mainColor;
    @JsonIgnore
    @OneToMany(mappedBy = "thumbnail")
    List<AbstractContent> contents = new ArrayList<>();
}
