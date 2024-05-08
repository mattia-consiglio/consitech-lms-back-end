package mattia.consiglio.consitech.lms.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.entities.MediaType;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.repositories.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MediaService {
    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private Cloudinary cloudinary;

    public Media uploadMedia(MultipartFile thumbnail) throws IOException {
        //check if media type is valid
//        try {
//            MediaType.valueOf(mediaType);
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
        String url = (String) response.get("url");
        System.out.println(response);
        System.out.println(response.get("colors"));
        Media media = new Media();
        media.setUrl(url);
        media.setCloudinaryPublicId(response.get("public_id").toString());
        media.setType(MediaType.valueOf(response.get("resource_type").toString().toUpperCase()));
        media.setWidth(Integer.parseInt(response.get("width").toString()));
        media.setHeight(Integer.parseInt(response.get("height").toString()));
        String hexColor = ((List<List<String>>) response.get("colors")).get(0).get(0);

        media.setMainColor(hexColor);
        media.setHash(response.get("etag").toString());
        return mediaRepository.save(media);
    }


    public Media getMedia(UUID id) {
        return mediaRepository.findById(id).orElseThrow(() -> new BadRequestException("Media not found"));
    }

    public void deleteMedia(UUID id) {
        mediaRepository.deleteById(id);
    }
}
