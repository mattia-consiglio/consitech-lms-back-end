package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.Seo;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.payloads.NewSeoDTO;
import mattia.consiglio.consitech.lms.payloads.UpdateSeoDTO;
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

    public Seo createSeo(NewSeoDTO newSeoDTO) {
        return seoRepository.save(new Seo(newSeoDTO.title(), newSeoDTO.description(), newSeoDTO.ldJson(), languageService.getLanguage(newSeoDTO.languageId())));
    }

    public Seo getSeo(UUID id) {
        return seoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Seo", id));
    }

    public Seo updateSeo(UUID id, UpdateSeoDTO seoDTO) {
        Seo seo = this.getSeo(id);
        seo.setTitle(seoDTO.title());
        seo.setDescription(seoDTO.description());
        seo.setLdJson(seoDTO.ldJson());
        return seoRepository.save(seo);
    }

    public void deleteSeo(UUID id) {
        Seo seo = this.getSeo(id);
        seoRepository.delete(seo);
    }
}
