package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private UUID id;
    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private MediaType type;
    private String alt;
    private int width;
    private int height;
    @JsonIgnore
    private String cloudinaryPublicId;
    private String mainColor;
    @JsonIgnore
    private String hash;
    private LocalDateTime uploadedAt;
    @JsonIgnore
    @OneToMany(mappedBy = "thumbnail")
    List<AbstractContent> contents = new ArrayList<>();
}
