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
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "media")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Media {
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
    @JsonIgnore
    private String hash;
    @JsonView(View.Public.class)
    private LocalDateTime uploadedAt;
}
