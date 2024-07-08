package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mattia.consiglio.consitech.lms.entities.enums.PublishStatus;
import mattia.consiglio.consitech.lms.utils.View;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "publish_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @JsonView(View.Admin.class)
    private PublishStatus publishStatus;

    @JsonView(View.Public.class)
    private LocalDateTime createdAt;

    @JsonView(View.Public.class)
    private long displayOrder;

    @ManyToOne(targetEntity = Media.class)
    @JoinColumn(name = "thumbnail_id")
    @JsonView(View.Public.class)
    private MediaImage thumbnailImage;

    @ManyToOne
    @JoinColumn(name = "seo_id")
    @JsonView(View.Internal.class)
    private Seo seo;

    public AbstractContent(Language mainLanguage, String title, String slug, String description, PublishStatus publishStatus, LocalDateTime createdAt, long displayOrder, MediaImage thumbnailImage, Seo seo) {
        super(mainLanguage);
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.publishStatus = publishStatus;
        this.createdAt = createdAt;
        this.displayOrder = displayOrder;
        this.thumbnailImage = thumbnailImage;
        this.seo = seo;
    }
}
