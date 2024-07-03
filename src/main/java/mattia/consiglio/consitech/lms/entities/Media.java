package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.*;
import mattia.consiglio.consitech.lms.utils.View;

import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@Table(name = "media")
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
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

    @JsonView(View.Admin.class)
    private String hash;

    @JsonView(View.Admin.class)
    @Column(name = "parent_id")
    private UUID parentId;

    @JsonView(View.Public.class)
    private LocalDateTime uploadedAt;

    @JsonView(View.Admin.class)
    private String filename;


    // Costruttore privato per il Builder
    protected Media(Builder<?> builder) {
        this.url = builder.url;
        this.type = builder.type;
        this.alt = builder.alt;
        this.hash = builder.hash;
        this.parentId = builder.parentId;
        this.uploadedAt = builder.uploadedAt;
        this.filename = builder.filename;
    }

    // Classe Builder
    public static abstract class Builder<T extends Builder<T>> {
        private String url;
        private MediaType type;
        private String alt;
        private String hash;
        private UUID parentId;
        private LocalDateTime uploadedAt;
        private String filename;

        public T url(String url) {
            this.url = url;
            return self();
        }

        public T type(MediaType type) {
            this.type = type;
            return self();
        }


        public T alt(String alt) {
            this.alt = alt;
            return self();
        }


        public T hash(String hash) {
            this.hash = hash;
            return self();
        }

        public T parentId(UUID parentId) {
            this.parentId = parentId;
            return self();
        }

        public T uploadedAt(LocalDateTime uploadedAt) {
            this.uploadedAt = uploadedAt;
            return self();
        }

        public T filename(String filename) {
            this.filename = filename;
            return self();
        }

        public T media(Media media) {
            this.url = media.url;
            this.type = media.type;
            this.alt = media.alt;
            this.hash = media.hash;
            this.parentId = media.parentId;
            this.uploadedAt = media.uploadedAt;
            this.filename = media.filename;
            return self();
        }

        protected abstract T self();

        public abstract Media build();
    }
}
