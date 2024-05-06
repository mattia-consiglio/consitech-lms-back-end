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
@Table(name = "languages")
public class Language {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.NONE)
    private UUID id;
    private String code; //it, en
    private String iso; //IT-it EN-en
    private String language;

    public Language(String code, String iso, String language) {
        this.code = code;
        this.iso = iso;
        this.language = language;
    }
}
