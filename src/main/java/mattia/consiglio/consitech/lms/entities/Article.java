package mattia.consiglio.consitech.lms.entities;

import jakarta.persistence.*;
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
    private String title;
    @Column(nullable = false)
    private String slug;
    private String description;
    @ManyToOne
    @JoinColumn(name = "thumbnail_id")
    private Media thumbnail;
    @Column(nullable = false)
    private String content;
}
