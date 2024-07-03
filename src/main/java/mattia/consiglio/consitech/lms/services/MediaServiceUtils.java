package mattia.consiglio.consitech.lms.services;

import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.Media;
import mattia.consiglio.consitech.lms.entities.MediaType;
import mattia.consiglio.consitech.lms.exceptions.BadRequestException;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.repositories.MediaRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@RequiredArgsConstructor
@Service
public class MediaServiceUtils {
    private final MediaRepository mediaRepository;
    @SuppressWarnings("SpringQualifierCopyableLombok")
    @Qualifier("mediaPath")
    private final String mediaPath;

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


    public File getMediaFile(Media media, boolean ignoreNotFound) {
        UUID parentId = media.getParentId();
        if (parentId != null) {
            media = this.getMedia(parentId);
        }
        return getFile(media, ignoreNotFound);
    }

    public File getMediaFile(Media media) {
        return getMediaFile(media, false);
    }

    private File getFile(Media media, boolean ignoreNotFound) {
        String filename = media.getFilename();
        String directoryPath = media.getType() == MediaType.VIDEO ? getVideoPath(filename) : mediaPath;
        File file = new File(directoryPath + File.separator + filename);

        try {
            if (!file.getCanonicalPath().startsWith(mediaPath)) {
                throw new ResourceNotFoundException("File", filename);
            }
        } catch (IOException e) {
            throw new ResourceNotFoundException("File", filename);
        }
        if (!ignoreNotFound && (!file.exists() || !file.isFile())) {
            throw new ResourceNotFoundException("File", filename);
        }
        return file;
    }


    public boolean isValidFileExtension(String fileExtension) {
        // Add valid file extensions here
        List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "mp4", "avi");
        return validExtensions.contains(fileExtension.toLowerCase());
    }

    public String getPath(Media media) {
        return mediaPath + File.separator + media.getFilename();
    }

    public String getPath(String filename) {
        return mediaPath + File.separator + filename;
    }

    public boolean isValidFilename(String filename) {
        if (filename == null ||
                !filename.contains(".") ||
                filename.contains("..") ||
                filename.startsWith(".") ||
                filename.endsWith(".")) {
            return false;
        }
        // validate filename
        String filenameRegex = "^[^.][a-zA-Z0-9_\\-.]+\\.(?:png|jpg|jpeg|mp4)$";
        if (!filename.matches(filenameRegex)) {
            return false;
        }
        Path path = Path.of(getPath(filename)).normalize();
        Path rootPath = Path.of(mediaPath).normalize();
        return path.startsWith(rootPath);
    }

    public String getVideoPath(String filename) {
        return mediaPath + File.separator + filename.replace(".", "_");
    }

    void deleteFile(File file) {
        boolean delete = tryDeleteFile(file);
        for (int i = 0; i < 4 || delete; i++) {
            delete = tryDeleteFile(file);
        }
    }

    boolean tryDeleteFile(File file) {
        if (file.exists()) {
            try {
                Files.delete(file.toPath());
                return true;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }
}
