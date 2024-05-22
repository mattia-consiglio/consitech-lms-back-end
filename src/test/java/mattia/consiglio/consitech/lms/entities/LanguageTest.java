package mattia.consiglio.consitech.lms.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageTest {
    @Test
    void testConstructor() {
        Language language = new Language("en", "English");
        assertNotNull(language);
        assertEquals("en", language.getCode());
        assertEquals("English", language.getLanguage());
    }

    @Test
    void testSetters() {
        Language language = new Language();
        language.setCode("fr");
        language.setLanguage("French");
        assertEquals("fr", language.getCode());
        assertEquals("French", language.getLanguage());
    }

    @Test
    void testNullInputs() {
        Language language = new Language(null, null);
        assertNull(language.getCode());
        assertNull(language.getLanguage());
    }

    @Test
    void testEmptyStringInputs() {
        Language language = new Language("", "");
        assertEquals("", language.getCode());
        assertEquals("", language.getLanguage());
    }
}