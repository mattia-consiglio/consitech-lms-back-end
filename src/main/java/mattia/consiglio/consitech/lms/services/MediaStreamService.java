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
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;

/**
 * Retrieves a media stream for the specified filename and HTTP request.
 */
@RequiredArgsConstructor
@Service
public class MediaStreamService {
    private final MediaService mediaService;
    private final MediaServiceUtils mediaServiceUtils;
    private final VideoResolutionsService videoResolutionsService;

    /**
     * Retrieves a media stream for the specified filename and HTTP request.
     *
     * @param filename the filename of the media to be streamed
     * @param request  the HTTP request containing the range header
     * @return a ResponseEntity containing the partial media stream and appropriate headers
     */
    public ResponseEntity<InputStreamResource> getMediaStream(String filename, HttpServletRequest request) {
        filename = filename.replaceAll("[^a-zA-Z0-9.-]", "_");
        if (!mediaServiceUtils.isValidFilename(filename)) {
            return null;
        }

        VideoResolution videoResolution = null;
        String newFilename = filename;
        if (filename.endsWith(".mp4")) {
            SimpleEntry<String, VideoResolution> result = parseVideoResolution(filename);
            newFilename = result.getKey();
            videoResolution = result.getValue();
        }

        Media media = mediaService.getMediaByFilename(newFilename);
        if (media == null) {
            return null;
        }

        File file = (videoResolution != null) ?
                mediaServiceUtils.getMediaFile(media, videoResolution) :
                mediaServiceUtils.getMediaFile(media);

        long fileSize = file.length();
        SimpleEntry<Long, Long> range = parseRange(request, fileSize);
        long start = range.getKey();
        long end = range.getValue();

        InputStream inputStream = getPartialMediaStream(file, start, end);
        String mimeType = determineMimeType(inputStream, filename);

        HttpHeaders headers = createHeaders(mimeType, start, end, fileSize);

        return new ResponseEntity<>(new InputStreamResource(inputStream), headers, HttpStatus.PARTIAL_CONTENT);
    }

    /**
     * Parses the video resolution from the given filename and returns the base filename and the corresponding VideoResolution object.
     *
     * @param filename the filename containing the video resolution information
     * @return a SimpleEntry containing the base filename and the VideoResolution object
     * @throws BadRequestException if the filename does not contain a valid video resolution
     */
    private SimpleEntry<String, VideoResolution> parseVideoResolution(String filename) {
        if (!filename.contains("_")) {
            throw new BadRequestException("Invalid filename. It must contain a video resolution");
        }
        String videoResolutionString = filename.substring(filename.lastIndexOf("_") + 1).replace(".mp4", "");
        VideoResolution videoResolution = videoResolutionsService.getVideoResolution(videoResolutionString);
        String newFilename = filename.substring(0, filename.lastIndexOf("_")).concat(".mp4");
        return new AbstractMap.SimpleEntry<>(newFilename, videoResolution);
    }

    /**
     * Parses the range header from the HTTP request and returns the start and end positions of the requested media stream.
     *
     * @param request  the HTTP request containing the range header
     * @param fileSize the total size of the media file in bytes
     * @return a SimpleEntry containing the start and end positions of the requested media stream
     * @throws BadRequestException if the range header is invalid or out of bounds
     */
    private SimpleEntry<Long, Long> parseRange(HttpServletRequest request, long fileSize) {
        long start = 0;
        long end = fileSize - 1;
        String range = request.getHeader("Range");
        if (range != null && range.startsWith("bytes=")) {
            String[] ranges = range.substring("bytes=".length()).split("-");
            start = Long.parseLong(ranges[0]);
            if (ranges.length > 1 && !ranges[1].isEmpty()) {
                end = Long.parseLong(ranges[1]);
            }
        }
        if (start >= fileSize || end >= fileSize || start > end) {
            throw new BadRequestException("Invalid range");
        }
        return new SimpleEntry<>(start, end);
    }


    /**
     * Determines the MIME type of the given input stream and filename.
     * <p>
     * If the MIME type cannot be guessed from the input stream, and the filename ends with ".mp4", the MIME type is set to "video/mp4".
     *
     * @param inputStream the input stream to determine the MIME type for
     * @param filename    the filename to use for determining the MIME type
     * @return the determined MIME type
     * @throws BadRequestException if there is an error while guessing the MIME type
     */
    private String determineMimeType(InputStream inputStream, String filename) {
        String mimeType;
        try {
            mimeType = URLConnection.guessContentTypeFromStream(inputStream);
        } catch (IOException e) {
            throw new BadRequestException("Error while guessing MIME type");
        }
        if (mimeType == null && filename.endsWith(".mp4")) {
            mimeType = "video/mp4";
        }
        return mimeType;
    }

    /**
     * Creates an HTTP response headers object with the appropriate content-related metadata for a partial media stream.
     *
     * @param mimeType the MIME type of the media file
     * @param start    the starting byte position of the requested media stream
     * @param end      the ending byte position of the requested media stream
     * @param fileSize the total size of the media file in bytes
     * @return an HttpHeaders object with the appropriate content-related metadata
     */
    private HttpHeaders createHeaders(String mimeType, long start, long end, long fileSize) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", mimeType);
        headers.add("Accept-Ranges", "bytes");
        headers.add("Content-Range", "bytes " + start + "-" + end + "/" + fileSize);
        headers.add("Content-Length", String.valueOf(end - start + 1));
        return headers;
    }


    /**
     * Retrieves a partial media stream from the specified file, based on the given start and end byte positions.
     *
     * @param file  the file containing the media stream
     * @param start the starting byte position of the requested media stream
     * @param end   the ending byte position of the requested media stream
     * @return an InputStream representing the partial media stream
     * @throws BadRequestException if there is an error while seeking the file
     */
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
