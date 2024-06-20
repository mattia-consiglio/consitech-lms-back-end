package mattia.consiglio.consitech.lms.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class Config {


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin(http -> http.disable()); //disable login form
        httpSecurity.csrf(csrf -> csrf.disable()); //disable CSRF protection
        httpSecurity.sessionManagement(http -> http.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); //disable session management, managed by JWT

        httpSecurity.cors(Customizer.withDefaults());
        httpSecurity.authorizeRequests(http -> http.requestMatchers("/**").permitAll());
        return httpSecurity.build();
    }

    @Bean
    PasswordEncoder getBCrypt() {
        return new BCryptPasswordEncoder(11);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://lms.consitech.it", "https://consitech-lms-front-end.vercel.app"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        // Registro la configurazione CORS appena fatta a livello globale su tutti gli endpoint del mio server

        return source;

    }

    @Bean
    public UserDetailsService userDetailsService() {
        // UserDetailsService è un'interfaccia che implementa un metodo per ottenere un oggetto UserDetails.

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String randomUsername = passwordEncoder.encode(UUID.randomUUID().toString());
        String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());

        UserDetails user = User.builder()
                .username(randomUsername)
                .password(randomPassword)
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}
