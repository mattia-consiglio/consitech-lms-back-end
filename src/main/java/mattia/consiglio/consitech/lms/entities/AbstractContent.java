package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mattia.consiglio.consitech.lms.utils.View;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

public abstract class AbstractContent extends TranslatableContent {
    @Column(nullable = false)
    @JsonView(View.Public.class)
    private String title;

    @Column(nullable = false, unique = true)
    @JsonView(View.Public.class)
    private String slug;

    @JsonView(View.Public.class)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @JsonView(View.Admin.class)
    private PublishStatus publishStatus;

    @JsonView(View.Public.class)
    private LocalDateTime createdAt;

    @JsonView(View.Public.class)
    private long displayOrder;

    @ManyToOne
    @JoinColumn(name = "thumbnail_id")
    @JsonView(View.Public.class)
    private Media thumbnail;

    @ManyToOne
    @JoinColumn(name = "seo_id")
    @JsonView(View.Internal.class)
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
