package mattia.consiglio.consitech.lms.config;


import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Bean(name = "allowedHosts")
    public List<String> allowedHosts() {
        return Arrays.asList("http://localhost:3000", "https://lms.consitech.it", "https://consitech-lms-front-end.vercel.app");
    }
}