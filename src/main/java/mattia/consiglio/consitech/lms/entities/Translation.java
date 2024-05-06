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
@Table(name = "translations")
public class Translation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.NONE)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "translation_language_id")
    private Language translationLanguage;
    private String tableName;
    private String fieldName;
    private String fieldValue;
    private UUID resource;
}
