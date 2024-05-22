package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "articles")
public class Article extends AbstractContent {
    @Column(nullable = false)
    private String content;
}
