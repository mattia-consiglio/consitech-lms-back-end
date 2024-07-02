package mattia.consiglio.consitech.lms.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mattia.consiglio.consitech.lms.entities.MediaVideo;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.repositories.MediaVideoRepository;
import mattia.consiglio.consitech.lms.utils.ProcessManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import static mattia.consiglio.consitech.lms.utils.GeneralChecks.checkUUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class MediaVideoService {
    private final MediaVideoRepository mediaVideoRepository;
    private final MediaServiceUtils mediaServiceUtils;
    private final VideoTranscodingService videoTranscodingService;


    public MediaVideo uploadVideo(MediaVideo media) {

        File fileVideo = mediaServiceUtils.getFile(media);

        double videoLength = getVideoDuration(fileVideo.getAbsolutePath());


        MediaVideo mediaImage = new MediaVideo.Builder()
                .media(media)
                .duration(videoLength)
                .build();
        media = mediaVideoRepository.save(mediaImage);

        // Transcode the video
        this.startTranscode(media);

        return media;

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

                log.error("Error transcoding video");
                log.error(Arrays.toString(e.getStackTrace()));
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}
