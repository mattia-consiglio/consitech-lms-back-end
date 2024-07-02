package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.MediaVideo;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.repositories.MediaVideoRepository;
import mattia.consiglio.consitech.lms.utils.ProcessManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@Service
public class MediaVideoService {
    private final MediaVideoRepository mediaVideoRepository;

    private final MediaServiceUtils mediaServiceUtils;

    private final VideoTranscodingService videoTranscodingService;


    @Autowired
    public MediaVideoService(MediaVideoRepository mediaVideoRepository, MediaServiceUtils mediaServiceUtils, VideoTranscodingService videoTranscodingService) {
        this.mediaVideoRepository = mediaVideoRepository;
        this.mediaServiceUtils = mediaServiceUtils;
        this.videoTranscodingService = videoTranscodingService;
    }


    public MediaVideo uploadVideo(MediaVideo media) {

        File fileVideo = mediaServiceUtils.getFile(media);

        double videoLength = getVideoDuration(fileVideo.getAbsolutePath());

        // Transcodifica il video
        this.startTranscode(media);

        MediaVideo mediaImage = new MediaVideo.Builder()
                .media(media)
                .duration(videoLength)
                .build();
        return mediaVideoRepository.save(mediaImage);

    }

    public long getVideoDuration(String filePath) {
        String[] command = {"ffprobe", "-v", "error", "-show_entries", "format=duration", "-of", "default=noprint_wrappers=1:nokey=1", filePath};
        long duration = 0;
        Map<String, String> output = ProcessManager.run(command);
        String durationString = output.get("output");

        if (durationString != null && !durationString.trim().isEmpty()) {
            durationString = durationString.trim();
            duration = Double.valueOf(durationString).longValue();
        }
        return duration;
    }

    public Map<String, Integer> getTranscodeProgress(String id) {
        UUID uuid = checkUUID(id, "media id");
        Map<String, Integer> progressMap = videoTranscodingService.getProgressMap();

        if (progressMap.containsKey(uuid.toString())) {
            return progressMap;
        } else {
            throw new ResourceNotFoundException("Media not found");
        }

    }

    public void startTranscode(MediaVideo media) {
        new Thread(() -> {
            try {
                videoTranscodingService.transcodeVideo(media);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
