package mattia.consiglio.consitech.lms.services;

import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.Language;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.payloads.LanguageDTO;
import mattia.consiglio.consitech.lms.repositories.LanguageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class LanguageService {
    private final LanguageRepository languageRepository;


    public Language createLanguage(LanguageDTO languageDTO) {
        return languageRepository.save(new Language(languageDTO.code(), languageDTO.language()));
    }

    public Language getLanguage(UUID id) {
        return languageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Language", id));
    }

    public Language getLanguageByCode(String code) {
        return languageRepository.findByCode(code).orElseThrow(() -> new ResourceNotFoundException("Language", "code", code));
    }

    public boolean existsByCode(String code) {
        return languageRepository.existsByCode(code);
    }

    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    public Language updateLanguage(UUID id, LanguageDTO languageDTO) {
        Language language = this.getLanguage(id);
        language.setLanguage(languageDTO.language());
        language.setCode(languageDTO.code());
        return languageRepository.save(language);
    }

    public void deleteLanguage(UUID id) {
        Language language = this.getLanguage(id);
        languageRepository.delete(language);
    }
}
