package mattia.consiglio.consitech.lms.services;

import lombok.Getter;
import mattia.consiglio.consitech.lms.entities.MediaVideo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Service
public class VideoTranscodingService {
    private final String mediaPath;
    private final String transcodePath;
    private final MediaServiceUtils mediaServiceUtils;

    @Autowired
    public VideoTranscodingService(@Qualifier("mediaPath") String mediaPath, @Qualifier("transcodePath") String transcodePath, MediaServiceUtils mediaServiceUtils) {
        this.mediaPath = mediaPath;
        this.transcodePath = transcodePath;
        this.mediaServiceUtils = mediaServiceUtils;
    }

    private final Map<String, Integer> progressMap = new ConcurrentHashMap<>();

    public void transcodeVideo(MediaVideo mediaVideo) throws IOException, InterruptedException {
        String filename = mediaVideo.getFilename();
        String id = String.valueOf(mediaVideo.getId());
        File sourceFile = new File(mediaPath, filename);
        String[] resolutions = {"1920x1080", "1280x720", "854x480", "640x360"};
        String[] qualitySettings = {"20", "23", "26", "29"}; // CRF values for each resolution

        mediaServiceUtils.ensureDirectoryExists(transcodePath);

        for (int i = 0; i < resolutions.length; i++) {
            String resolution = resolutions[i];
            String crf = qualitySettings[i];
            String outputFilePath = new File(transcodePath, filename.replace(".mp4", "_" + resolution + ".mp4")).getAbsolutePath();

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-i", sourceFile.getAbsolutePath(),
                    "-vf", "scale=" + resolution.replace("x", ":"), // Set the resolution
                    "-c:v", "libx264",
                    "-preset", "fast",
                    "-crf", crf, // Set the CRF value
                    "-c:a", "aac",
                    "-nostats", // Suppress regular output
                    "-progress", "-", // Output progress information to stdout
                    "-hide_banner", // Suppress banner
                    outputFilePath
            );
            processBuilder.redirectErrorStream(true);
            try {
                Process process = processBuilder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("frame=")) {
                        String[] progressInfo = line.split("=")[1].trim().split(" ");
                        int progress = Integer.parseInt(progressInfo[0].trim());
                        progressMap.put(id, progress);
                    }
                }

                // Log error output
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    System.err.println("FFmpeg error: " + errorLine);
                }

                int exitCode = process.waitFor();
                if (exitCode != 0) {
                    throw new RuntimeException("Error during video transcoding, exit code: " + exitCode);
                }
                System.out.println("Transcoding complete");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
