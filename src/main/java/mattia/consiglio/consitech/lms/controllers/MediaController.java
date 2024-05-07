package mattia.consiglio.consitech.lms.controllers;

import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;
import static mattia.consiglio.consitech.lms.utities.GeneralChecks.checkUUID;

@RestController
@RequestMapping(BASE_URL + "/media")
public class MediaController {
    @Autowired
    private MediaService mediaService;


    @PostMapping("/upload")
    public Media uploadMedia(@RequestParam("thumbnail") MultipartFile thumbnail,
                             @RequestPart("mediaType") String mediaDTO, BindingResult validation) throws IOException {
        if (validation.hasErrors()) {
            throw new BadRequestException("Invalid data", validation.getAllErrors());
        }
        return mediaService.uploadMedia(thumbnail, mediaDTO);
    }

    @GetMapping("{id}")
    public Media getMediaById(@PathVariable("id") String id) {
        UUID uuid = checkUUID(id, "id");
        return mediaService.getMedia(uuid);
    }
}
