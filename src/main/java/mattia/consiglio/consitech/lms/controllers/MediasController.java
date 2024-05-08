package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public Media uploadMedia(@RequestParam("thumbnail") MultipartFile thumbnail) throws IOException {
        return mediaService.uploadMedia(thumbnail);
    }

    @GetMapping("{id}")
    public Media getMediaById(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        return mediaService.getMedia(uuid);
    }
}
