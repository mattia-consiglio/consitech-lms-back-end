package mattia.consiglio.consitech.lms.services;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.entities.MediaImage;
import mattia.consiglio.consitech.lms.entities.MediaType;
import mattia.consiglio.consitech.lms.entities.MediaVideo;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.payloads.UpdateMediaDTO;
import mattia.consiglio.consitech.lms.repositories.AbstractContentRepository;
import mattia.consiglio.consitech.lms.repositories.MediaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@Slf4j
@Service
public class MediaService {
    private static final Logger logger = LoggerFactory.getLogger(MediaService.class);

    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private AbstractContentRepository abstractContentRepository;

    @Autowired
    private MediaImageService mediaImageService;

    @Autowired
    private MediaVideoService mediaVideoService;

    @Autowired
    private HttpServletRequest request;

    public Media uploadMedia(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;

        // Extract file extension
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        // Remove file extension
        String filename = originalFilename.substring(0, originalFilename.lastIndexOf("."));

        String alt = filename;
        alt = alt.replaceAll("-", " ").replaceAll("\\s+", " ").trim();
        System.out.println("alt: " + alt);

        // Sanitize filename
        filename = filename.toLowerCase();
        filename = Normalizer.normalize(filename, Normalizer.Form.NFKD)
                .replaceAll("[^a-zA-Z0-9]+", "-")
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

        if (mediaDifference.isDifferent()) saveFile(file, newFilename);

        // Build media url
        String url = getHostUrl() + "/media/" + newFilename;

        MediaType mediaType = getMediaType(file);

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
                return mediaImageService.uploadImage(mediaImage, file);

            case VIDEO:
                MediaVideo mediaVideo = new MediaVideo.Builder().url(url)
                        .type(mediaType)
                        .alt(alt)
                        .hash(hash)
                        .uploadedAt(LocalDateTime.now())
                        .filename(newFilename)
                        .parentId(mediaDifference.getParentId())
                        .build();
                return mediaVideoService.uploadVideo(mediaVideo, file);
            default:
                throw new BadRequestException("Invalid media type");
        }

    }

    public void saveFile(MultipartFile file, String newFilename) {
        //get application root path
        String rootPath = System.getProperty("user.dir");

        //save file on media folder
        File mediaFolder = new File(rootPath + File.separator + "media");
        if (!mediaFolder.exists()) mediaFolder.mkdir();

        File mediaFile = new File(mediaFolder, newFilename);
        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(mediaFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            logger.error("Error transferring file to media folder", e);
            throw new RuntimeException(e);
        }

        // Check if the file exists
        if (!mediaFile.exists()) {
            logger.error("File does not exist: {}", mediaFile.getAbsolutePath());
            throw new RuntimeException("File does not exist: " + mediaFile.getAbsolutePath());
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
        Pattern pattern = Pattern.compile(regex);

        final int[] index = {0};
        final boolean[] found = {false};

        mediaList.forEach((Media m) -> {
            if (m.getFilename().replaceAll(regex, "").equals(filename) && m.getParentId() != null) {
                if (index[0] == 0) {
                    found[0] = true;
                }
                String mediaFilename = m.getFilename();
                Matcher matcher = pattern.matcher(mediaFilename);
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

        if (index[0] == 0 && !found[0])
            return new MediaDifference(isDifferent, filename + "." + fileExtension, parentMedia.getId());
        index[0]++;

        return new MediaDifference(isDifferent, filename + "-" + index[0] + "." + fileExtension, parentMedia.getId());
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
        UUID uuid = checkUUID(id, "media id");
        return this.getMedia(uuid);
    }

    public Media getMedia(UUID id) {
        if (id == null) {
            throw new BadRequestException("Media id cannot be null");
        }
        return mediaRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Media", id));
    }

    public Media getMediaByFilename(String filename) {
        if (filename == null) {
            throw new BadRequestException("Media filename cannot be null");
        }
        return mediaRepository.findByFilename(filename).orElseThrow(() -> new ResourceNotFoundException("Media", filename));
    }

    public File getFile(Media media) {
        String rootPath = System.getProperty("user.dir");
        String filename = media.getFilename();
        System.out.println("media: " + media);
        UUID parentId = media.getParentId();
        if (parentId != null) {
            Media parentMedia = this.getMedia(parentId);
            filename = parentMedia.getFilename();
        }

        return new File(rootPath + File.separator + "media" + File.separator + filename);
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
        if (media.getType() == MediaType.IMAGE) {
            MediaImage mediaImage = (MediaImage) media;

            mediaImage.getContents().forEach(abstractContent -> {
                abstractContent.setThumbnailImage(null);
                abstractContentRepository.save(abstractContent);
            });
            try {
//                cloudinary.api().deleteResources(Collections.singletonList(mediaImage.getCloudinaryPublicId()),
//                        ObjectUtils.asMap("type", "upload", "resource_type", "image"));
            } catch (Exception exception) {
                throw new BadRequestException("Error deleting file form Cloudinary. " + exception.getMessage());
            }
        }
        mediaRepository.delete(media);
    }

    public Page<Media> getAllMedia(int page, int size, String sort, String direction) {
        Sort.Direction sortDirection = Sort.Direction.ASC; // Default sort direction

        if (direction != null && direction.equalsIgnoreCase("desc")) {
            sortDirection = Sort.Direction.DESC;
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        return mediaRepository.findAll(pageable);
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
     * Calculates the MD5 hash of a file.
     *
     * @param file The file to calculate the hash of.
     * @return The calculated hash.
     * @throws IOException              If an I/O error occurs.
     * @throws NoSuchAlgorithmException If the MessageDigest algorithm is not found.
     */
    public static String calculateHash(MultipartFile file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("MD5");
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
}
