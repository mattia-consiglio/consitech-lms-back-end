package mattia.consiglio.consitech.lms.services;

import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.entities.VideoResolution;
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
    private final VideoResolutionsService videoResolutionsService;

    public ResponseEntity<InputStreamResource> getMediaStream(String filename) {
        if (!mediaServiceUtils.isValidFilename(filename)) {
            throw new BadRequestException("Invalid filename");
        }


        if (filename == null) {
            return null;
        }

        VideoResolution videoResolution = null;
        String newFilename = filename;
        if (filename.endsWith(".mp4")) {
            if (filename.contains("_")) {
                String videoResolutionString = filename.substring(filename.lastIndexOf("_") + 1).replace(".mp4", "");
                videoResolution = videoResolutionsService.getVideoResolution(videoResolutionString);
                newFilename = filename.substring(0, filename.lastIndexOf("_")).concat(".mp4");
            } else {
                throw new BadRequestException("Invalid filename. It must contain a video resolution");
            }
        }

        Media media = mediaService.getMediaByFilename(newFilename);
        if (media == null) {
            return null;
        }
        File file;
        if (videoResolution != null) {
            file = mediaServiceUtils.getMediaFile(media, videoResolution);
        } else {
            file = mediaServiceUtils.getMediaFile(media);
        }

        System.out.println("File: " + file.getAbsolutePath());

        InputStream inputStream;

        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            throw new BadRequestException("Error while opening file");
        }

        String mimeType;
        try {
            mimeType = URLConnection.guessContentTypeFromStream(inputStream);
            System.out.println("MIME type: " + mimeType);
        } catch (IOException e) {
            throw new BadRequestException("Error while guessing MIME type");
        }
        System.out.println("MIME type: " + mimeType);
        
        if (mimeType == null && filename.endsWith(".mp4")) {
            mimeType = "video/mp4";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", mimeType);

        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.OK);
    }


}
