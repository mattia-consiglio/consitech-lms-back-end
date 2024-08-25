package mattia.consiglio.consitech.lms.controllers;

import jakarta.servlet.http.HttpServletRequest;
import mattia.consiglio.consitech.lms.services.MediaStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("media")
public class MediaStreamController {
    @Autowired
    private MediaStreamService mediaStreamService;

    @GetMapping("{filename}")
    public ResponseEntity<InputStreamResource> getMediaStream(@PathVariable("filename") String filename, HttpServletRequest request) {
        return mediaStreamService.getMediaStream(filename, request);
    }
}
