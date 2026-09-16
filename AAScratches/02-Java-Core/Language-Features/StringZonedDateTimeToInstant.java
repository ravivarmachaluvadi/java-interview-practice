import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

class StringZonedDateTimeToInstant {
    public static void main(String[] args) {
        String s = "2025-11-12 23:31:00.000 +0530";
        DateTimeFormatter dateTimeFormatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");
        OffsetDateTime offsetDateTime =
                OffsetDateTime.parse(s, dateTimeFormatter);
        Instant instant = offsetDateTime.toInstant();
        System.out.println(instant);
    }
}
