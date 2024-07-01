package mattia.consiglio.consitech.lms.config;


import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ServerConfig {

    @Bean
    public Cloudinary cloudinaryUploader(@Value("${cloudinary.name}") String name,
                                         @Value("${cloudinary.key}") String key,
                                         @Value("${cloudinary.secret}") String secret) {
        Map<String, String> configuration = new HashMap<>();
        configuration.put("cloud_name", name);
        configuration.put("api_key", key);
        configuration.put("api_secret", secret);
        return new Cloudinary(configuration);
    }

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
}