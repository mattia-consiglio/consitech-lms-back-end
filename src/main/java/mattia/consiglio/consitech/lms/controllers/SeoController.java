package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.Seo;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.NewSeoDTO;
import mattia.consiglio.consitech.lms.payloads.UpdateSeoDTO;
import mattia.consiglio.consitech.lms.services.SeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;
import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@RestController
@RequestMapping(BASE_URL + "/seo")
public class SeoController {
    @Autowired
    private SeoService seoService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Seo createSeo(@Validated @RequestBody NewSeoDTO newSeoDTO, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return seoService.createSeo(newSeoDTO);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Seo updateSeo(@PathVariable("id") String id, @Validated @RequestBody UpdateSeoDTO seoDTO, BindingResult validation) {
        UUID uuid = checkUUID(id, "id");
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return seoService.updateSeo(uuid, seoDTO);
    }

    @GetMapping("{id}")
    public Seo getSeoById(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        return seoService.getSeo(uuid);
    }
}
