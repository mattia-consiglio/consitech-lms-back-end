package mattia.consiglio.consitech.lms.controllers;

import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.payloads.UpdateMediaDTO;
import mattia.consiglio.consitech.lms.services.MediaService;
import mattia.consiglio.consitech.lms.services.MediaVideoService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static mattia.consiglio.consitech.lms.controllers.BaseController.BASE_URL;

@RestController
@RequestMapping(BASE_URL + "/media")
@RequiredArgsConstructor
public class MediasController {
    private final MediaService mediaService;
    private final MediaVideoService mediaVideoService;

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Media uploadMedia(@RequestParam("thumbnail") MultipartFile thumbnail) {
        return mediaService.uploadMedia(thumbnail);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Iterable<Media> getMedia(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size,
                                    @RequestParam(defaultValue = "uploadedAt") String sort,
                                    @RequestParam(defaultValue = "desc") String direction,
                                    @RequestParam(required = false) String type) {
        return mediaService.getAllMedia(page, size, sort, direction, type);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Media getMediaById(@PathVariable("id") String id) {
        return mediaService.getMedia(id);
    }

    @GetMapping("/{id}/transcode-progress")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Map<String, Integer> getTranscodeProgress(@PathVariable("id") String id) {
        return mediaVideoService.getTranscodeProgress(id);
    }

    @PostMapping("/{id}/transcode")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void transcodeMedia(@PathVariable("id") String id) {
        mediaService.transcodeVideo(id);
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
