package mattia.consiglio.consitech.lms.services;

import lombok.Getter;
import mattia.consiglio.consitech.lms.entities.MediaVideo;
import mattia.consiglio.consitech.lms.entities.VideoResolutions;
import mattia.consiglio.consitech.lms.utils.LineProcessor;
import mattia.consiglio.consitech.lms.utils.ProcessManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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


        if (!sourceFile.exists()) {
            throw new IOException("File " + filename + " does not exist");
        }

        List<String> resolutions = new ArrayList<>();
        String[] qualitySettings = {"20", "23", "26", "29"}; // CRF values for each resolution

        for (VideoResolutions resolution : VideoResolutions.values()) {
            resolutions.add(resolution.getWidth() + "x" + resolution.getHeight());
        }

        mediaServiceUtils.ensureDirectoryExists(transcodePath);

        LineProcessor lineProcessor = (String line) -> {

            if (line.startsWith("frame=")) {
                String[] progressInfo = line.split("=")[1].trim().split(" ");
                int progress = Integer.parseInt(progressInfo[0].trim());
                progressMap.put(id, progress);
            }
        };

        for (int i = 0; i < resolutions.size(); i++) {
            String resolution = resolutions.get(i);
            String crf = qualitySettings[i];
            String outputFilePath = new File(transcodePath, filename.replace(".mp4", "_" + resolution + ".mp4")).getAbsolutePath();

            String[] command = {"ffmpeg",
                    "-i", sourceFile.getAbsolutePath(),
                    "-vf", "scale=" + resolution.replace("x", ":"),
                    "-c:v", "libx264",
                    "-preset", "fast",
                    "-crf", crf,
                    "-c:a", "aac",
                    "-nostats",
                    "-progress", "-",
                    "-hide_banner",
                    outputFilePath};

            ProcessManager.run(command, lineProcessor);
            System.out.println("Transcoding video " + filename + " to " + resolution + " completed.");
        }
    }
}
