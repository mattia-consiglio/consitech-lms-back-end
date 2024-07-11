package mattia.consiglio.consitech.lms.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mattia.consiglio.consitech.lms.entities.MediaVideo;
import mattia.consiglio.consitech.lms.exceptions.ResourceNotFoundException;
import mattia.consiglio.consitech.lms.repositories.LessonRepository;
import mattia.consiglio.consitech.lms.repositories.MediaVideoRepository;
import mattia.consiglio.consitech.lms.utils.ProcessManager;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
    private final LessonRepository lessonRepository;


    public MediaVideo uploadVideo(MediaVideo media) {

        File fileVideo = mediaServiceUtils.getMediaFile(media);

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


    public void deleteVideo(MediaVideo media) {
        updateLessons(media);

        if (media.getParentId() == null) {
            List<MediaVideo> mediaList = mediaVideoRepository.findByParentId(media.getId());
            mediaList.forEach((MediaVideo m) -> {
                updateLessons(m);
                mediaVideoRepository.delete(m);
            });
            mediaServiceUtils.deleteFile(mediaServiceUtils.getMediaFile(media, true));
        }
        mediaVideoRepository.delete(media);
    }

    private void updateLessons(MediaVideo media) {
        media.getLessons().forEach(lesson -> {
            lesson.setVideo(media);
            lessonRepository.save(lesson);
        });
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
                videoTranscodingService.startTranscodeVideo(media);
            } catch (IOException e) {
                log.error("Error transcoding video");
                log.error(Arrays.toString(e.getStackTrace()));
                Thread.currentThread().interrupt();
            }
        }).start();
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
}