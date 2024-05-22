package mattia.consiglio.consitech.lms.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import mattia.consiglio.consitech.lms.validations.ValueOfEnumMessageInterpolator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ValidationConfig {
    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ValueOfEnumMessageInterpolator())
                .buildValidatorFactory();

        return validatorFactory.getValidator();
    }
}