package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.Language;
import mattia.consiglio.consitech.lms.payloads.LanguageDTO;
import mattia.consiglio.consitech.lms.services.LanguageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;
import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@RestController
@RequestMapping(BASE_URL + "/languages")
public class LanguageController {
    @Autowired
    private LanguageService languageService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
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
    public Language getLanguageById(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        return languageService.getLanguage(uuid);
    }

    @GetMapping("code/{code}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Language getLanguageByCode(@PathVariable("code") String code) {
        return languageService.getLanguageByCode(code);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Language updateLanguage(@PathVariable("id") String id, @RequestBody LanguageDTO language) {
        UUID uuid = checkUUID(id, "id");
        return languageService.updateLanguage(uuid, language);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLanguage(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        languageService.deleteLanguage(uuid);
    }
}
