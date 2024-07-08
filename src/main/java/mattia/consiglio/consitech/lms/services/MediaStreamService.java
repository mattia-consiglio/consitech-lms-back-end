package mattia.consiglio.consitech.lms.services;

import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URLConnection;

@RequiredArgsConstructor
@Service
public class MediaStreamService {
    private final MediaService mediaService;
    private final MediaServiceUtils mediaServiceUtils;

    public ResponseEntity<InputStreamResource> getMediaStream(String filename) {
        if (!mediaServiceUtils.isValidFilename(filename)) {
            throw new BadRequestException("Invalid filename");
        }

        Media media = mediaService.getMediaByFilename(filename);
        if (media == null) {
            return null;
        }


        File file = mediaServiceUtils.getMediaFile(media);

        InputStream inputStream;

        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new BadRequestException("Error while opening file");
        }

        String mimeType;
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
