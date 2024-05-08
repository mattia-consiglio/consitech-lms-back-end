package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.Seo;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.SeoDTO;
import mattia.consiglio.consitech.lms.repositories.SeoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SeoService {
    @Autowired
    private SeoRepository seoRepository;

    @Autowired
    private LanguageService languageService;

    public Seo createSeo(SeoDTO seoDTO) {
        return seoRepository.save(new Seo(seoDTO.title(), seoDTO.description(), seoDTO.ldJson(), languageService.getLanguage(seoDTO.languageId())));
    }

    public Seo getSeo(UUID id) {
        return seoRepository.findById(id).orElseThrow(() -> new BadRequestException("Seo not found"));
    }

    public Seo updateSeo(UUID id, SeoDTO seoDTO) {
        Seo seo = this.getSeo(id);
        seo.setTitle(seoDTO.title());
        seo.setDescription(seoDTO.description());
        seo.setLdJson(seoDTO.ldJson());
        seo.setMainLanguage(languageService.getLanguage(seoDTO.languageId()));
        return seoRepository.save(seo);
    }

    public void deleteSeo(UUID id) {
        Seo seo = this.getSeo(id);
        seoRepository.delete(seo);
    }
}
