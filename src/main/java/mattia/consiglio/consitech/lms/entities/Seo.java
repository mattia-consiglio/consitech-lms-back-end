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
@Table(name = "seo")
public class Seo extends TranslatableContent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.NONE)
    private UUID id;
    private String title;
    private String description;
    @Column(name = "ld_json")
    private String ldJson;

    public Seo(String title, String description, String ldJson, Language mainLanguage) {
        super(mainLanguage);
        this.title = title;
        this.description = description;
        this.ldJson = ldJson;
    }
}
