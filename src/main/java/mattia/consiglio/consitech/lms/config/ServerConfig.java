package mattia.consiglio.consitech.lms.config;


import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.VideoResolution;
import mattia.consiglio.consitech.lms.services.VideoResolutionsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class ServerConfig {
    private final VideoResolutionsService videoResolutionsService;

    @Bean("allowedHosts")
    public List<String> allowedHosts() {
        return Arrays.asList(
                "http://localhost:3000",
                "https://lms.consitech.it",
                "https://consitech-lms-front-end.vercel.app",
                "https://consitech-lms-front-end-git-develop-mattias-projects-67143469.vercel.app"
        );
    }

    /**
     * Returns the path to the media files directory.
     *
     * @return the path to the media files directory
     */
    @Bean("mediaPath")
    public String mediaPath() {
        String rootPath = System.getProperty("user.dir");
        if (!rootPath.endsWith(File.separator)) {
            rootPath += File.separator;
        }
        return rootPath + "mediaFiles";
    }

    @Bean("transcodePath")
    public String transcodePath() {
        return mediaPath() + File.separator + "transcode";
    }

    @Bean
    public List<VideoResolution> videoResolutions() {
        return videoResolutionsService.getVideoResolutions();
    }
}