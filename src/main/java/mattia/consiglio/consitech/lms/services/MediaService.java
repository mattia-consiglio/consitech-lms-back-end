package mattia.consiglio.consitech.lms.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.entities.MediaImage;
import mattia.consiglio.consitech.lms.entities.MediaType;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.payloads.UpdateMediaDTO;
import mattia.consiglio.consitech.lms.repositories.AbstractContentRepository;
import mattia.consiglio.consitech.lms.repositories.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@Service
public class MediaService {
    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private AbstractContentRepository abstractContentRepository;

    @Autowired
    private Cloudinary cloudinary;

    public Media uploadMedia(MultipartFile thumbnail) throws IOException {
        //check if media type is valid
//        try {
//            MediaType.valueOf(alt);
//        } catch (IllegalArgumentException e) {
//            String mediaTypeValues = Stream.of(MediaType.values())
//                    .map(Enum::name)
//                    .collect(Collectors.joining(", "));
//            throw new BadRequestException("Value must be one of the following: " + mediaTypeValues);
//        }

//        System.out.println(thumbnail.getContentType());
        String filename = thumbnail.getOriginalFilename();
        assert filename != null;
        filename = filename.toLowerCase();
        filename = Normalizer.normalize(filename, Normalizer.Form.NFKD);
        //remove extension
        filename = filename.substring(0, filename.lastIndexOf("."));
        filename = filename.replaceAll("[^a-zA-Z0-9]+", "-");
        filename = filename.replaceAll("-{2,}", "-");
        filename = filename.replaceAll("-$", "");
        filename = filename.replaceAll("^-", "");


        System.out.println(thumbnail.getOriginalFilename());

        Map response = cloudinary.uploader().upload(thumbnail.getBytes(), ObjectUtils.asMap(
                "public_id", filename,
                "unique_filename", true,
                "colors", true,
                "folder", "media",
                "overwrite", false
        ));
        String url = (String) response.get("secure_url");
        System.out.println(response);
        System.out.println(response.get("colors"));
        MediaImage media = new MediaImage();
        media.setUrl(url);
        media.setUploadedAt(LocalDateTime.now());
        media.setCloudinaryPublicId(response.get("public_id").toString());
        media.setType(MediaType.valueOf(response.get("resource_type").toString().toUpperCase()));
        media.setWidth(Integer.parseInt(response.get("width").toString()));
        media.setHeight(Integer.parseInt(response.get("height").toString()));
        String hexColor = ((List<List<String>>) response.get("colors")).get(0).get(0);

        media.setMainColor(hexColor);
        media.setHash(response.get("etag").toString());
        return mediaRepository.save(media);
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
                abstractContent.setThumbnail(null);
                abstractContentRepository.save(abstractContent);
            });
            try {
                cloudinary.api().deleteResources(Collections.singletonList(mediaImage.getCloudinaryPublicId()),
                        ObjectUtils.asMap("type", "upload", "resource_type", "image"));
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
//                    media.setUrl(resource.get("secure_url").toString());
//                    media.setUploadedAt(LocalDateTime.now());
//                    media.setCloudinaryPublicId(publicId);
//                    media.setType(MediaType.valueOf(resource.get("resource_type").toString().toUpperCase()));
//                    media.setWidth(Integer.parseInt(resource.get("width").toString()));
//                    media.setHeight(Integer.parseInt(resource.get("height").toString()));
////                    media.setMainColor(((List<List<String>>) resource.get("colors")).get(0).get(0));
////                    media.setHash(resource.get("etag").toString());
//                    mediaRepository.save(media);
//                }
//            }
        } catch (Exception e) {
            throw new BadRequestException("Error syncing media from Cloudinary. " + e.getMessage());
        }
    }
}
