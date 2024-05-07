package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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
    private String title;
    @Column(nullable = false)
    private String slug;
    private String description;
    @Column(nullable = false)
    private PublishStatus publishStatus;
    private LocalDateTime createdAt;
    private int displayOrder;
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
