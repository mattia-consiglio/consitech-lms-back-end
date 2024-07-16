package mattia.consiglio.consitech.lms.services;

import jakarta.servlet.http.HttpServletRequest;
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

    public ResponseEntity<InputStreamResource> getMediaStream(String filename, HttpServletRequest request) {
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

        long fileSize = file.length();
        long start = 0;
        long end = fileSize - 1;

        String range = request.getHeader("Range");
        if (range != null && range.startsWith("bytes=")) {
            range = range.substring("bytes=".length());
            String[] ranges = range.split("-");
            start = Long.parseLong(ranges[0]);
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                end = Long.parseLong(ranges[1]);
            }
        }

        if (start >= fileSize || end >= fileSize || start > end) {
            throw new BadRequestException("Invalid range");
        }

        InputStream inputStream = getPartialMediaStream(file, start, end);

        String mimeType;
        try {
            mimeType = URLConnection.guessContentTypeFromStream(inputStream);
            System.out.println("MIME type: " + mimeType);
        } catch (IOException e) {
            throw new BadRequestException("Error while guessing MIME type");
        }

        if (mimeType == null && filename.endsWith(".mp4")) {
            mimeType = "video/mp4";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", mimeType);
        headers.add("Accept-Ranges", "bytes");
        headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
        headers.add("Content-Length", String.valueOf(end - start + 1));

        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.PARTIAL_CONTENT);
    }

    private InputStream getPartialMediaStream(File file, long start, long end) {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r")) {
            byte[] data = new byte[(int) (end - start + 1)];
            randomAccessFile.seek(start);
            randomAccessFile.readFully(data);
            return new ByteArrayInputStream(data);
        } catch (IOException e) {
            throw new BadRequestException("Error while seeking file");
        }
    }

}
