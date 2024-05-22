package mattia.consiglio.consitech.lms.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, CharSequence> {
    private List<String> acceptedValues;
    private String enumValues;

    @Override
    public void initialize(ValueOfEnum annotation) {
        acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
                .map(Enum::name)
                .map(String::toUpperCase)
                .collect(Collectors.toList());

        enumValues = ValueOfEnum.Helper.getEnumValues(annotation.enumClass());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean isValid = acceptedValues.contains(value.toString().toUpperCase());
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate().replaceAll("\\{0\\}", enumValues))
                    .addConstraintViolation();
        }

        return isValid;
    }
}
