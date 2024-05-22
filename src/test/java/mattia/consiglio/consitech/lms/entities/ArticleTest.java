package mattia.consiglio.consitech.lms.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ArticleTest {
    private Article article;

    @BeforeEach
    void setUp() {
        article = new Article();
    }

    @Test
    void testGettersAndSetters() {
        UUID id = UUID.randomUUID();
        String title = "Test Article";
        String slug = "test-article";
        String description = "This is a test article";

        String content = "This is the content of the test article";
        Seo seo = new Seo();
        Language language = new Language();
        Media thumbnail = new Media();

        article.setTitle(title);
        article.setSlug(slug);
        article.setDescription(description);
        article.setThumbnail(thumbnail);
        article.setContent(content);
        article.setSeo(seo);
        article.setMainLanguage(language);

        assertEquals(title, article.getTitle());
        assertEquals(slug, article.getSlug());
        assertEquals(description, article.getDescription());
        assertEquals(thumbnail, article.getThumbnail());
        assertEquals(content, article.getContent());
        assertEquals(seo, article.getSeo());
        assertEquals(language, article.getMainLanguage());
    }

    @Test
    void testNoArgsConstructor() {
        assertNotNull(article);
    }
}