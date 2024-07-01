package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.repositories.MediaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@Service
public class MediaServiceUtils {
    @Autowired
    private MediaRepository mediaRepository;

    public void ensureDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
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

    public File getFile(Media media) {
        String rootPath = System.getProperty("user.dir");
        String filename = media.getFilename();
        UUID parentId = media.getParentId();
        if (parentId != null) {
            Media parentMedia = this.getMedia(parentId);
            filename = parentMedia.getFilename();
        }


        return new File(rootPath + File.separator + "media" + File.separator + filename);
    }

    public boolean isValidFileExtension(String fileExtension) {
        // Add valid file extensions here
        List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "mp4", "avi");
        return validExtensions.contains(fileExtension.toLowerCase());
    }

}
