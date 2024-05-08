package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AbstractContent extends TranslatableContent {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String slug;
    private String description;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PublishStatus publishStatus;
    private LocalDateTime createdAt;
    private long displayOrder;
    @ManyToOne
    @JoinColumn(name = "thumbnail_id")
    private Media thumbnail;
    @ManyToOne
    @JoinColumn(name = "seo_id")
    private Seo seo;

    public AbstractContent(Language mainLanguage, String title, String slug, String description, PublishStatus publishStatus, LocalDateTime createdAt, long displayOrder, Media thumbnail, Seo seo) {
        super(mainLanguage);
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.publishStatus = publishStatus;
        this.createdAt = createdAt;
        this.displayOrder = displayOrder;
        this.thumbnail = thumbnail;
        this.seo = seo;
    }
}
