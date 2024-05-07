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
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MediaService {
    @Autowired
    private MediaRepository mediaRepository;

    @Autowired
    private Cloudinary cloudinary;

    public Media uploadMedia(MultipartFile thumbnail, String mediaType) throws IOException {
        //check if media type is valid
        try {
            MediaType.valueOf(mediaType);
        } catch (IllegalArgumentException e) {
            String mediaTypeValues = Stream.of(MediaType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            throw new BadRequestException("Value must be one of the following: " + mediaTypeValues);
        }
        String url = (String) cloudinary.uploader().upload(thumbnail.getBytes(), ObjectUtils.emptyMap()).get("url");
        Media media = new Media();
        media.setUrl(url);
        media.setType(MediaType.valueOf(mediaType));
        return mediaRepository.save(media);
    }


    public Media getMedia(UUID id) {
        return mediaRepository.findById(id).orElseThrow(() -> new BadRequestException("Media not found"));
    }
}
