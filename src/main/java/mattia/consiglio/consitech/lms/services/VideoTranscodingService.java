package mattia.consiglio.consitech.lms.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mattia.consiglio.consitech.lms.entities.MediaVideo;
import mattia.consiglio.consitech.lms.entities.VideoResolutions;
import mattia.consiglio.consitech.lms.repositories.MediaVideoRepository;
import mattia.consiglio.consitech.lms.utils.LineProcessor;
import mattia.consiglio.consitech.lms.utils.ProcessManager;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Getter
@RequiredArgsConstructor
@Service
public class VideoTranscodingService {
    @Qualifier("mediaPath")
    private final String mediaPath;
    @Qualifier("transcodePath")
    private final String transcodePath;
    private final MediaServiceUtils mediaServiceUtils;
    private final MediaVideoRepository mediaVideoRepository;

    private final Map<String, Integer> progressMap = new ConcurrentHashMap<>();

    public void transcodeVideo(MediaVideo mediaVideo) throws IOException, InterruptedException {
        String filename = mediaVideo.getFilename();
        String id = String.valueOf(mediaVideo.getId());
        File sourceFile = new File(mediaPath, filename);
        if (!sourceFile.exists()) {
            throw new IOException("File " + filename + " does not exist");
        }

        String[] getResulutionCommand = {"ffprobe", "-v", "error", "-select_streams", "v:0", "-show_entries", "stream=width,height", "-of", "csv=p=0", sourceFile.getAbsolutePath()};

        String videoResolution = ProcessManager.run(getResulutionCommand).get("output");
        if (videoResolution != null && !videoResolution.contains(",")) {
            throw new BadRequestException("Cannot determine video resolution");
        }
        videoResolution = videoResolution.replace("\n", "");
        String[] videoResolutionList = videoResolution.split(",");
        int width = Integer.parseInt(videoResolutionList[0]);

        String[] getVideoFramesCommand = {"ffprobe", "-v", "error", "-select_streams", "v:0", "-count_packets", "-show_entries", "stream=nb_read_packets", "-of", "csv=p=0", sourceFile.getAbsolutePath()};
        String videoFramesString = ProcessManager.run(getVideoFramesCommand).get("output");
        if (videoFramesString == null) {
            throw new BadRequestException("Cannot determine video frames");
        }
        videoFramesString = videoFramesString.replace("\n", "");
        int videoFrames = Integer.parseInt(videoFramesString);


        List<VideoResolutions> resolutionsEnum = new ArrayList<>();
        List<String> resolutions = new ArrayList<>();
        List<Integer> qualitySettings = new ArrayList<>();
        List<String> resolutionsNames = new ArrayList<>();


        for (VideoResolutions resolution : VideoResolutions.values()) {
            if (width < resolution.getWidth()) {
                continue;
            }
            resolutions.add(resolution.getWidth() + ":-2");
            qualitySettings.add(resolution.getQuality());
            resolutionsNames.add(resolution.getName());
            resolutionsEnum.add(resolution);
        }

        mediaServiceUtils.ensureDirectoryExists(transcodePath);

        LineProcessor lineProcessor = (String line) -> {
            if (line.startsWith("frame=")) {
                String[] progressInfo = line.split("=")[1].trim().split(" ");
                int progressFrames = Integer.parseInt(progressInfo[0].trim()) == 0 ? 0 : Integer.parseInt(progressInfo[0].trim());
                int progress = progressFrames == 0 ? 0 : videoFrames / progressFrames;
                progressMap.put(id, progress);
            }
        };

        for (int i = 0; i < resolutions.size(); i++) {
            String resolution = resolutions.get(i);
            String crf = qualitySettings.get(i).toString();
            String resolutionName = resolutionsNames.get(i);
            String outputFilePath = new File(transcodePath, filename.replace(".mp4", "_" + resolutionName + ".mp4")).getAbsolutePath();

            String[] command = {"ffmpeg",
                    "-i", sourceFile.getAbsolutePath(),
                    "-vf", "scale=" + resolution,
                    "-c:v", "libx264",
                    "-preset", "fast",
                    "-crf", crf,
                    "-c:a", "aac",
                    "-nostats",
                    "-progress", "-",
                    "-hide_banner",
                    outputFilePath};

            ProcessManager.run(command, lineProcessor);

            mediaVideo.setResolutions(resolutionsEnum);
            mediaVideoRepository.save(mediaVideo);

            log.info("Transcoding video " + filename + " to " + resolutionName + " completed.");
        }
    }
}
