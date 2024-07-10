package mattia.consiglio.consitech.lms.runners;

import lombok.RequiredArgsConstructor;
import mattia.consiglio.consitech.lms.entities.VideoResolution;
import mattia.consiglio.consitech.lms.entities.enums.UserRole;
import mattia.consiglio.consitech.lms.payloads.EditUserDTO;
import mattia.consiglio.consitech.lms.payloads.LanguageDTO;
import mattia.consiglio.consitech.lms.services.LanguageService;
import mattia.consiglio.consitech.lms.services.UserService;
import mattia.consiglio.consitech.lms.services.VideoResolutionsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class DatabaseInitializer implements CommandLineRunner {
    private final LanguageService languageService;
    private final UserService userService;
    private final VideoResolutionsService videoResolutionsService;

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        if (!languageService.existsByCode("it")) {
            languageService.createLanguage(new LanguageDTO("it", "Italiano"));
        }
        if (!languageService.existsByCode("en")) {
            languageService.createLanguage(new LanguageDTO("en", "English"));
        }

        if (userService.countUsersByRole(UserRole.ADMIN) < 1) {
            userService.createUser(new EditUserDTO(adminUsername, adminPassword, adminEmail, UserRole.ADMIN.name()));
        }

        List<VideoResolution> videoResolutions = videoResolutionsService.getVideoResolutions();
        if (videoResolutions.isEmpty()) {
            videoResolutionsService.createVideoResolution(new VideoResolution("1440p", 2560, 1440, 24));
            videoResolutionsService.createVideoResolution(new VideoResolution("1080p", 1920, 1080, 22));
            videoResolutionsService.createVideoResolution(new VideoResolution("720p", 1280, 720, 21));
            videoResolutionsService.createVideoResolution(new VideoResolution("480p", 854, 480, 20));
            videoResolutionsService.createVideoResolution(new VideoResolution("360p", 640, 360, 19));
            videoResolutionsService.createVideoResolution(new VideoResolution("240p", 426, 240, 17));
        }
    }
}
