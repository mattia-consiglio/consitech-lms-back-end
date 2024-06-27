package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.UpdateMediaDTO;
import mattia.consiglio.consitech.lms.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;
import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@RestController
@RequestMapping(BASE_URL + "/media")
public class MediasController {
    @Autowired
    private MediaService mediaService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Media uploadMedia(@RequestParam("thumbnail") MultipartFile thumbnail) {
        return mediaService.uploadMedia(thumbnail);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Iterable<Media> getMedia(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size, @RequestParam(defaultValue = "uploadedAt") String sort, @RequestParam(defaultValue = "desc") String direction) {
        return mediaService.getAllMedia(page, size, sort, direction);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Media getMediaById(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        return mediaService.getMedia(uuid);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Media updateMedia(@PathVariable("id") String id, @Validated @RequestBody UpdateMediaDTO mediaDTO, BindingResult validation) {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return mediaService.updateMedia(id, mediaDTO);
    }


    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMedia(@PathVariable("id") String id) {
        mediaService.deleteMedia(id);
    }
}
