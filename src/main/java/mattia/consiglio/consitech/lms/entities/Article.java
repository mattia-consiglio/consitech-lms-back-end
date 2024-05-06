package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "articles")
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.NONE)
    private UUID id;
    private String title;
    private String slug;
    private String description;
    private String thumbnail;
    private String content;
    @ManyToOne
    @JoinColumn(name = "seo_id")
    private Seo seo;
    @ManyToOne
    @JoinColumn(name = "main_language_id")
    private Language mainLanguage;
}
