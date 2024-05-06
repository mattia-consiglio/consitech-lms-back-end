package mattia.consiglio.consitech.lms.validations;

import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validation;

import java.util.Locale;

public class ValueOfEnumMessageInterpolator implements MessageInterpolator {
    private final MessageInterpolator defaultInterpolator;

    public ValueOfEnumMessageInterpolator() {
        this.defaultInterpolator = Validation.byDefaultProvider()
                .configure()
                .getDefaultMessageInterpolator();
    }

    @Override
    public String interpolate(String messageTemplate, Context context) {
        String interpolatedMessage = defaultInterpolator.interpolate(messageTemplate, context);

        if (context.getConstraintDescriptor().getAnnotation() instanceof ValueOfEnum valueOfEnum) {
            String enumValues = ValueOfEnum.Helper.getEnumValues(valueOfEnum.enumClass());
            interpolatedMessage = interpolatedMessage.replace("{0}", enumValues);
        }

        return interpolatedMessage;
    }

    @Override
    public String interpolate(String messageTemplate, Context context, Locale locale) {
        return interpolate(messageTemplate, context);
    }
}
