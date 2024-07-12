package mattia.consiglio.consitech.lms.services;

import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.entities.MediaImage;
import mattia.consiglio.consitech.lms.entities.MediaVideo;
import mattia.consiglio.consitech.lms.entities.VideoResolution;
import mattia.consiglio.consitech.lms.entities.enums.MediaType;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.payloads.UpdateMediaDTO;
import mattia.consiglio.consitech.lms.repositories.AbstractContentRepository;
import mattia.consiglio.consitech.lms.repositories.LessonRepository;
import mattia.consiglio.consitech.lms.repositories.MediaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@RequiredArgsConstructor
@Service
public class MediaService {
    private final MediaRepository mediaRepository;
    private final AbstractContentRepository abstractContentRepository;
    private final LessonRepository lessonRepository;
    private final MediaImageService mediaImageService;
    private final MediaVideoService mediaVideoService;
    private final HttpServletRequest request;
    private final MediaServiceUtils mediaServiceUtils;
    @SuppressWarnings("SpringQualifierCopyableLombok")
    @Qualifier("mediaPath")
    private final String mediaPath;

    public Media uploadMedia(MultipartFile file) {
        if (file.isEmpty()) {
            throw new BadRequestException("Invalid file content");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BadRequestException("Invalid file name");
        }

        // Sanitize the filename
        String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9\\._]+", "-");

        // Extract file extension
        String fileExtension = sanitizedFilename.substring(sanitizedFilename.lastIndexOf(".") + 1);
        // Remove file extension
        String filename = sanitizedFilename.substring(0, sanitizedFilename.lastIndexOf("."));

        // Validate the file extension
        if (!mediaServiceUtils.isValidFileExtension(fileExtension)) {
            throw new BadRequestException("Invalid file extension");
        }

        String alt = filename.replace("-", " ").replaceAll("\\s+", " ").trim();

        // Sanitize filename
        filename = filename.toLowerCase();
        filename = Normalizer.normalize(filename, Normalizer.Form.NFKD)
                .replaceAll("-{2,}", "-")
                .replaceAll("-$", "")
                .replaceAll("^-", "");


        String hash = null;
        try {
            hash = calculateHash(file);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        MediaDifference mediaDifference = checkFileDifference(hash, filename, fileExtension);

        String newFilename = mediaDifference.getFilename();

        MediaType mediaType = getMediaType(file);

        if (mediaDifference.isDifferent()) saveFile(file, newFilename, mediaType);

        // Build media url
        String url = getHostUrl() + "/media/" + newFilename;


        switch (mediaType) {
            case IMAGE:
                MediaImage mediaImage = new MediaImage.Builder()
                        .url(url)
                        .type(mediaType)
                        .alt(alt)
                        .hash(hash)
                        .uploadedAt(LocalDateTime.now())
                        .filename(newFilename)
                        .parentId(mediaDifference.getParentId())
                        .build();
                return mediaImageService.uploadImage(mediaImage);

            case VIDEO:
                MediaVideo mediaVideo = new MediaVideo.Builder().url(url)
                        .type(mediaType)
                        .alt(alt)
                        .hash(hash)
                        .uploadedAt(LocalDateTime.now())
                        .filename(newFilename)
                        .parentId(mediaDifference.getParentId())
                        .build();
                return mediaVideoService.uploadVideo(mediaVideo);
            default:
                throw new BadRequestException("Invalid media type");
        }

    }

    public void saveFile(MultipartFile file, String newFilename, MediaType mediaType) {

        String destinationPath = mediaType == MediaType.VIDEO ? mediaServiceUtils.getVideoPath(newFilename) : mediaPath;
        mediaServiceUtils.ensureDirectoryExists(destinationPath);

        File mediaFile = new File(destinationPath, newFilename);
        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(mediaFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Check if the file exists
        if (!mediaFile.exists()) {
            throw new ResourceNotFoundException("File does not exist: " + mediaFile.getAbsolutePath());
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    private static class MediaDifference {
        private boolean isDifferent;
        private String filename;
        private UUID parentId;

        public MediaDifference(boolean isDifferent, String filename) {
            this.isDifferent = isDifferent;
            this.filename = filename;
            this.parentId = null;
        }
    }

    private MediaDifference checkFileDifference(String hash, String filename, String fileExtension) {
        boolean isDifferent = true;
        List<Media> mediaList = mediaRepository.findByHashOrderByFilenameDesc(hash);

        if (mediaList.isEmpty()) return new MediaDifference(isDifferent, filename + "." + fileExtension);

        Media parentMedia = mediaList.stream().filter(m -> m.getParentId() == null).findFirst().orElse(null);
        if (parentMedia == null) return new MediaDifference(isDifferent, filename + "." + fileExtension);

        isDifferent = false;

        String regex = "-(\\d+)\\." + fileExtension + "$|\\." + fileExtension + "$";


        Map<String, Integer> mediaIndex = getLastMediaIndex(mediaList, regex, filename, isDifferent, fileExtension, parentMedia);

        if (mediaIndex.get("index") == 0 && mediaIndex.get("found") == 0) {
            return new MediaDifference(isDifferent, filename + "." + fileExtension, parentMedia.getId());
        }

        return new MediaDifference(isDifferent, filename + "-" + mediaIndex.get("index") + "." + fileExtension, parentMedia.getId());
    }

    private Map<String, Integer> getLastMediaIndex(List<Media> mediaList, String regex, String filename, boolean isDifferent, String fileExtension, Media parentMedia) {
        final int[] index = {0};
        final boolean[] found = {false};
        final Map<String, Integer> output = new HashMap<>();

        mediaList.forEach((Media m) -> {
            if (m.getFilename().replaceAll(regex, "").equals(filename) && m.getParentId() != null) {
                if (index[0] == 0) {
                    found[0] = true;
                }
                String mediaFilename = m.getFilename();
                Matcher matcher = Pattern.compile(regex).matcher(mediaFilename);
                if (matcher.find()) {
                    if (matcher.group(1) != null) {
                        String indexString = matcher.group(1);
                        int mediaIndex = Integer.parseInt(indexString);
                        if (mediaIndex > index[0]) {
                            index[0] = mediaIndex;
                        }
                    }
                }
            }
        });

        if (index[0] == 0 && !found[0]) {
            output.put("index", index[0]);
            output.put("found", 0);
            return output;
        }

        index[0]++;
        output.put("index", index[0]);
        output.put("found", 1);
        return output;
    }

    private String getHostUrl() {

        String scheme = request.getScheme();             // http or https
        String serverName = request.getServerName();     // hostname or IP
        int serverPort = request.getServerPort();        // port number
        String contextPath = request.getContextPath();   // application context path

        // Construct the full URL
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        // Append the port if it's not the default port
        if ((scheme.equals("http") && serverPort != 80) || (scheme.equals("https") && serverPort != 443)) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath);

        return url.toString();
    }

    public Media getMedia(String id) {
        return mediaServiceUtils.getMedia(id);
    }

    public Media getMedia(UUID id) {
        return mediaServiceUtils.getMedia(id);
    }

    public Media getMediaByFilename(String filename) {
        if (filename == null) {
            throw new BadRequestException("Media filename cannot be null");
        }
        return mediaRepository.findByFilename(filename).orElseThrow(() -> new ResourceNotFoundException("Media", filename));
    }


    public Media updateMedia(String id, UpdateMediaDTO mediaDTO) {
        UUID uuid = checkUUID(id, "media id");
        return this.updateMedia(uuid, mediaDTO);
    }

    public Media updateMedia(UUID id, UpdateMediaDTO mediaDTO) {
        Media media = this.getMedia(id);
        media.setAlt(mediaDTO.alt());
        return mediaRepository.save(media);
    }

    public void deleteMedia(String id) {
        UUID uuid = checkUUID(id, "media id");
        this.deleteMedia(uuid);
    }

    public void deleteMedia(UUID id) {
        Media media = this.getMedia(id);

        updateMediaContents(media);

        if (media.getParentId() == null) {

            List<Media> mediaList = mediaRepository.findByParentId(media.getId());
            mediaList.forEach(m -> {
                updateMediaContents(m);
                mediaRepository.delete(m);
            });
            if (media.getType() == MediaType.IMAGE) {
                this.deleteImageFile(media);
            } else {
                this.deleteVideoFile(media);
            }
        }
        mediaRepository.delete(media);
    }

    private void deleteImageFile(Media media) {
        mediaServiceUtils.deleteFile(mediaServiceUtils.getMediaFile(media, true));
    }

    private void deleteVideoFile(Media media) {
        MediaVideo mediaVideo = (MediaVideo) media;
        List<VideoResolution> resolutions = mediaVideo.getResolutions();
        resolutions.forEach(resolution -> mediaServiceUtils.deleteFile(mediaServiceUtils.getMediaFile(media, true, resolution)));
        mediaServiceUtils.deleteFile(new File(mediaServiceUtils.getVideoPath(mediaVideo.getFilename())));
    }

    private void updateMediaContents(Media media) {
        if (media.getType() == MediaType.IMAGE) {
            this.updateImageContents(media);
        } else {
            this.updateVideoLessons(media);
        }
    }

    private void updateImageContents(Media media) {
        MediaImage mediaImage = (MediaImage) media;
        mediaImage.getContents().forEach(abstractContent -> {
            abstractContent.setThumbnailImage(null);
            abstractContentRepository.save(abstractContent);
        });
    }

    private void updateVideoLessons(Media media) {
        MediaVideo mediaVideo = (MediaVideo) media;
        mediaVideo.getLessons().forEach(lesson -> {
            lesson.setVideo(null);
            lessonRepository.save(lesson);
        });
    }

    public Page<Media> getAllMedia(int page, int size, String sort, String direction, String type) {
        Sort.Direction sortDirection = Sort.Direction.ASC; // Default sort direction


        if (direction != null && direction.equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        Specification<Media> mediaSpecification = getMediaSpecification(type);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return mediaRepository.findAll(mediaSpecification, pageable);
    }

    public Specification<Media> getMediaSpecification(String type) {

        return (root, query, cb) -> {
            Predicate p = cb.conjunction();

            if (type != null) {
                MediaType mediaType;
                try {
                    mediaType = MediaType.valueOf(type);
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid media type: " + type);
                }
                p = cb.and(p, cb.equal(root.get("type"), mediaType));

            }
            return p;
        };
    }

    public void syncMedia() {
        //get all media from cloudinary and save them in the database in case they are not already there
        try {
//            ApiResponse apiResponse = cloudinary.api().resourceByAssetID("85673c8286be9af8e0a1aee250035460?colors=true", ObjectUtils.asMap(
//                    "colors", true
//            ));
//            System.out.println(apiResponse);


//            ApiResponse response = cloudinary.api().resourcesByAssetFolder("media", ObjectUtils.asMap(
//                    "tags", true,
//                    "metadata", true
//            ));
//            List<Map> resources = (List<Map>) response.get("resources");
//            System.out.println("resources " + resources);
//            for (Map resource : resources) {
//                String publicId = resource.get("public_id").toString();
//                Media media = mediaRepository.findByCloudinaryPublicId(publicId);
//                if (media == null) {
//                    media = new Media();
//                    media.url(resource.get("secure_url").toString());
//                    media.setUploadedAt(LocalDateTime.now());
//                    media.setCloudinaryPublicId(publicId);
//                    media.setType(MediaType.valueOf(resource.get("resource_type").toString().toUpperCase()));
//                    media.setWidth(Integer.parseInt(resource.get("width").toString()));
//                    media.setHeight(Integer.parseInt(resource.get("height").toString()));
////                    media.setAvgColor(((List<List<String>>) resource.get("colors")).get(0).get(0));
////                    media.setHash(resource.get("etag").toString());
//                    mediaRepository.save(media);
//                }
//            }
        } catch (Exception e) {
            throw new BadRequestException("Error syncing media from Cloudinary. " + e.getMessage());
        }
    }

    /**
     * Calculates the SHA-256 hash of a file.
     *
     * @param file The file to calculate the hash of.
     * @return The calculated hash.
     * @throws IOException              If an I/O error occurs.
     * @throws NoSuchAlgorithmException If the MessageDigest algorithm is not found.
     */
    public static String calculateHash(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        InputStream inputStream = file.getInputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;

        while ((bytesRead = inputStream.read(buffer)) != -1) {
            digest.update(buffer, 0, bytesRead);
        }

        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();

        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }


    public MediaType getMediaType(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null) {
            if (contentType.startsWith("image")) {
                return MediaType.IMAGE;
            } else if (contentType.startsWith("video")) {
                return MediaType.VIDEO;
            } else if (contentType.startsWith("audio")) {
                return MediaType.AUDIO;
            }
        }
        return null;
    }

    public void transcodeVideo(String id) {
        Media media = this.getMedia(id);
        if (media.getType() == MediaType.VIDEO) {
            mediaVideoService.startTranscode((MediaVideo) media);
        }
    }
}
