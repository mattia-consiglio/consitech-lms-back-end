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
        return Arrays.asList("http://localhost:3000", "https://lms.consitech.it", "https://consitech-lms-front-end.vercel.app");
    }

    @Bean("mediaPath")
    public String mediaPath() {
        String rootPath = System.getProperty("user.dir");
        return rootPath + File.separator + "media";
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