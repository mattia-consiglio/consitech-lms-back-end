package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.Language;
import mattia.consiglio.consitech.lms.payloads.LanguageDTO;
import mattia.consiglio.consitech.lms.services.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/languages")
public class LanguageController {
    @Autowired
    private LanguageService languageService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Language createLanguage(LanguageDTO language) {
        return languageService.createLanguage(language);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Language> getAllLanguages() {
        return languageService.getAllLanguages();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Language getLanguageById(@PathVariable("id") UUID id) {
        return languageService.getLanguage(id);
    }

    @GetMapping("code/{code}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Language getLanguageByCode(@PathVariable("code") String code) {
        return languageService.getLanguageByCode(code);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Language updateLanguage(@PathVariable("id") UUID id, @RequestBody LanguageDTO language) {
        return languageService.updateLanguage(id, language);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteLanguage(@PathVariable("id") UUID id) {
        languageService.deleteLanguage(id);
    }
}
