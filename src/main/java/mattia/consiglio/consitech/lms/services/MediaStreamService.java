package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLConnection;

@Service
public class MediaStreamService {
    @Autowired
    private MediaService mediaService;

    public ResponseEntity<InputStreamResource> getMediaStream(String filename) {
        Media media = mediaService.getMediaByFilename(filename);
        if (media == null) {
            return null;
        }

        File file = mediaService.getFile(media);

        InputStream inputStream = null;

        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new BadRequestException("Error while opening file");
        }

        String mimeType = null;
        try {
            mimeType = URLConnection.guessContentTypeFromStream(inputStream);
        } catch (IOException e) {
            throw new BadRequestException("Error while guessing MIME type");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", mimeType);

        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
    }
}
