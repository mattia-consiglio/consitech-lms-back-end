package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractContent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.NONE)
    private UUID id;
    @Column(nullable = false)
    private PublishStatus publishStatus;
    @ManyToOne
    @JoinColumn(name = "thumbnail_id")
    private Media thumbnail;
    @ManyToOne
    @JoinColumn(name = "seo_id")
    private Seo seo;
    @ManyToOne
    @JoinColumn(name = "main_language_id")
    private Language mainlanguage;
}
