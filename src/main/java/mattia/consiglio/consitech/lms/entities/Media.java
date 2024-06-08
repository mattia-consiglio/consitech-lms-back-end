package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mattia.consiglio.consitech.lms.utils.View;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "media")
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.NONE)
    @JsonView(View.Public.class)
    private UUID id;
    @Column(nullable = false)
    @JsonView(View.Public.class)
    private String url;
    @Column(nullable = false)
    @JsonView(View.Public.class)
    private MediaType type;
    @JsonView(View.Public.class)
    private String alt;
    @JsonView(View.Public.class)
    private int width;
    @JsonView(View.Public.class)
    private int height;
    @JsonIgnore
    private String cloudinaryPublicId;
    @JsonView(View.Public.class)
    private String mainColor;
    @JsonIgnore
    private String hash;
    @JsonView(View.Public.class)
    private LocalDateTime uploadedAt;
    @JsonIgnore
    @OneToMany(mappedBy = "thumbnail")
    List<AbstractContent> contents = new ArrayList<>();
}
