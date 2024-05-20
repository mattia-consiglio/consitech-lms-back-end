package mattia.consiglio.consitech.lms.entities;

import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mattia.consiglio.consitech.lms.utils.View;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class TranslatableContent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(value = AccessLevel.NONE)
    @JsonView(View.Public.class)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "main_language_id")
    private Language mainLanguage;

    @OneToMany(mappedBy = "resource")
    private List<Translation> translations = new ArrayList<>();

    public TranslatableContent(Language mainLanguage) {
        this.mainLanguage = mainLanguage;
    }
}
