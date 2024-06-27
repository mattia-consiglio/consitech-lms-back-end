package mattia.consiglio.consitech.lms.services;

import mattia.consiglio.consitech.lms.entities.MediaVideo;
import mattia.consiglio.consitech.lms.repositories.MediaVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
public class MediaVideoService {
    @Autowired
    private MediaVideoRepository mediaVideoRepository;

    @Autowired
    private MediaServiceUtils mediaServiceUtils;


    public MediaVideo uploadVideo(MediaVideo media, MultipartFile file) {
        try {
            InputStream inputStream = file.getInputStream();

            File fileVideo = mediaServiceUtils.getFile(media);
            double videoLength = getVideoDuration(fileVideo.getAbsolutePath());

            MediaVideo mediaImage = new MediaVideo.Builder()
                    .media(media)
                    .duration(videoLength)
                    .build();
            return mediaVideoRepository.save(mediaImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public long getVideoDuration(String filePath) {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-i", filePath, "-hide_banner"
        );
        processBuilder.redirectErrorStream(true);

        try {
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Duration:")) {
                    String duration = line.split("Duration:")[1].split(",")[0].trim();
                    return parseDuration(duration);
                }
            }
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error while getting video duration", e);
        }
        return 0;
    }

    private long parseDuration(String duration) {
        String[] parts = duration.split(":");
        long hours = Long.parseLong(parts[0]);
        long minutes = Long.parseLong(parts[1]);
        double seconds = Double.parseDouble(parts[2]);
        return (hours * 3600) + (minutes * 60) + (long) seconds;
    }
}
