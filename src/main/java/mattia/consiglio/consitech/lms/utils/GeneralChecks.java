package mattia.consiglio.consitech.lms.utils;

import java.util.UUID;

public final class GeneralChecks {
    public static UUID checkUUID(String uuid, String fieldName) {
        try {
            return UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("The value of " + fieldName + " is not a valid UUID");
        }
    }
}
