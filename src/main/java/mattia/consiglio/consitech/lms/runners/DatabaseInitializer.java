package mattia.consiglio.consitech.lms.runners;

import mattia.consiglio.consitech.lms.entities.Language;
import mattia.consiglio.consitech.lms.entities.UserRole;
import mattia.consiglio.consitech.lms.payloads.EditUserDTO;
import mattia.consiglio.consitech.lms.payloads.LanguageDTO;
import mattia.consiglio.consitech.lms.services.LanguageService;
import mattia.consiglio.consitech.lms.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    @Autowired
    private LanguageService languageService;

    @Autowired
    private UserService userService;

    @Value("${admin.email}")
    private String adminEmail;
    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        Language englishLanguage;
        Language italianLanguage;
        if (!languageService.existsByCode("it")) {
            italianLanguage = languageService.createLanguage(new LanguageDTO("it", "Italiano"));
        }
//        if (!languageService.existsByCode("en")) {
//           englishLanguage = languageService.createLanguage(new LanguageDTO("en", "English"));
//        }

        if (userService.countUsersByRole(UserRole.ADMIN) < 1) {
            userService.createUser(new EditUserDTO(adminUsername, adminPassword, adminEmail, UserRole.ADMIN.name()));
        }
    }
}
