package mattia.consiglio.consitech.lms.runners;

import mattia.consiglio.consitech.lms.services.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MediaSyncRunner implements CommandLineRunner {
    @Autowired
    private MediaService mediaService;

    @Override
    public void run(String... args) throws Exception {
        mediaService.syncMedia();
    }
}
