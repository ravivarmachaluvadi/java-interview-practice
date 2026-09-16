/**
 * Converts a string representation of a zoned date-time into an {@link java.time.Instant}.
 *
 * The input format is "yyyy-MM-dd HH:mm:ss.SSS Z" (e.g., "2025-11-12 23:31:00.000 +0530").
 * A {@link java.time.format.DateTimeFormatter} parses the string into an {@link java.time.OffsetDateTime},
 * which is then converted to an {@link java.time.Instant}.
 *
 * Approach:
 * 1. Define a formatter matching the input pattern.
 * 2. Parse the string into an OffsetDateTime using that formatter.
 * 3. Convert the resulting OffsetDateTime to an Instant and output it.
 *
 * Time Complexity: O(1) – parsing and conversion are constant‑time operations for a fixed length string.
 * Space Complexity: O(1) – only a few objects of fixed size are created.
 */
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
