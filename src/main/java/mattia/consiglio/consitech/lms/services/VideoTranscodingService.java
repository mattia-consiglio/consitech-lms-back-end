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
    @SuppressWarnings("SpringQualifierCopyableLombok")
    @Qualifier("mediaPath")
    private final String mediaPath;
    private final MediaServiceUtils mediaServiceUtils;
    private final MediaVideoRepository mediaVideoRepository;


    private final Map<String, Integer> progressMap = new ConcurrentHashMap<>();

    public void startTranscodeVideo(MediaVideo mediaVideo) throws IOException {
        String filename = mediaVideo.getFilename();
        String videoPath = mediaServiceUtils.getVideoPath(filename);
        File sourceFile = new File(videoPath, filename);
        if (!sourceFile.exists()) {
            throw new IOException("File " + filename + " does not exist");
        }

        String id = String.valueOf(mediaVideo.getId());

        int width = getResolution(sourceFile);
        int videoFrames = getFrames(sourceFile);

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


        mediaServiceUtils.ensureDirectoryExists(videoPath);

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
            String outputFilePath = new File(videoPath, filename.replace(".mp4", "_" + resolutionName + ".mp4")).getAbsolutePath();


            ffmpegTranscode(sourceFile, resolution, crf, outputFilePath, lineProcessor);
            mediaVideo.setResolutions(resolutionsEnum);
            mediaVideoRepository.save(mediaVideo);

            log.info("Transcoding video {} to {} completed.", filename, resolutionName);
        }
        progressMap.remove(id);
        log.info("Transcoding video {} completed.", filename);
        mediaServiceUtils.deleteFile(sourceFile);
    }

    private void ffmpegTranscode(File sourceFile, String resolution, String crf, String outputFilePath, LineProcessor lineProcessor) {
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
    }

    private int getResolution(File sourceFile) throws BadRequestException {
        String[] command = {"ffprobe", "-v", "error", "-select_streams", "v:0", "-show_entries", "stream=width", "-of", "csv=p=0", sourceFile.getAbsolutePath()};

        String videoResolution = ProcessManager.run(command).get("output");
        if (videoResolution == null) {
            throw new BadRequestException("Cannot determine video resolution");
        }
        videoResolution = videoResolution.replace("\n", "");
        return Integer.parseInt(videoResolution);
    }

    private int getFrames(File sourceFile) throws BadRequestException {
        String[] command = {"ffprobe", "-v", "error", "-select_streams", "v:0", "-count_packets", "-show_entries", "stream=nb_read_packets", "-of", "csv=p=0", sourceFile.getAbsolutePath()};
        String videoFramesString = ProcessManager.run(command).get("output");
        if (videoFramesString == null) {
            throw new BadRequestException("Cannot determine video frames");
        }
        videoFramesString = videoFramesString.replace("\n", "");
        return Integer.parseInt(videoFramesString);
    }
}
