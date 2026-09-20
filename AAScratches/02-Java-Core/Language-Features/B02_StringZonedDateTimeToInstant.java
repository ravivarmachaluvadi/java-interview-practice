/*
 * =====================================================================
 *  Parsing an offset date-time string into an Instant   Java Core | Easy
 * =====================================================================
 *
 * WHAT THIS DEMONSTRATES
 *   Turning text like "2025-11-12 23:31:00.000 +0530" into a java.time.Instant.
 *   The string carries a UTC offset, so it names a real point on the timeline;
 *   the job is to parse it with a DateTimeFormatter into an OffsetDateTime and
 *   then drop the offset with toInstant().
 *
 * WHAT YOU WILL SEE
 *   "2025-11-12 23:31:00.000 +0530" -> 2025-11-12T18:01:00Z   (23:31 minus 5h30)
 *   "2025-11-12 23:31:00.000 -0800" -> 2025-11-13T07:31:00Z   (23:31 plus 8h)
 *   "2025-11-12 18:01:00.000 +0000" -> 2025-11-12T18:01:00Z   same instant as case 1
 *   "2025-11-12 23:31:00.000"       -> DateTimeParseException  no offset in the text
 *
 * HOW IT WORKS
 *   1. Build a DateTimeFormatter whose pattern mirrors the text exactly:
 *      "yyyy-MM-dd HH:mm:ss.SSS Z".  Z means an RFC-822 offset such as +0530.
 *   2. OffsetDateTime.parse(text, formatter) gives local fields PLUS the offset.
 *   3. toInstant() subtracts the offset, leaving a UTC point in time.
 *   4. Instant.toString() always prints in UTC and ends in Z.
 *
 * KEY INSIGHT
 *   Pick the java.time type by how much the text actually tells you.
 *   LocalDateTime = wall clock with no offset, so it is NOT a point in time.
 *   OffsetDateTime = wall clock plus a fixed offset, so it is.
 *   ZonedDateTime = wall clock plus a zone id, so it also knows DST rules.
 *   Instant = the point in time alone. Store Instant, display ZonedDateTime.
 *
 * GOTCHAS
 *   - Pattern letters are case sensitive: "mm" is minutes, "MM" is months, and
 *     "hh" is a 12-hour clock that needs an am/pm field.
 *   - "Z" in the pattern parses +0530; "XXX" parses +05:30; "VV" parses
 *     Asia/Kolkata. Use the one that matches your input.
 *   - Two different offset strings can denote the SAME instant, so compare
 *     Instants (or use isEqual), never the formatted text.
 *   - Parsing text with no offset into OffsetDateTime throws - it would have to
 *     invent a zone. Parse it as LocalDateTime and attach a zone on purpose.
 *
 * INTERVIEW FOLLOW-UPS
 *   - How do you go from Instant back to a user's local time? (atZone(zoneId))
 *   - Why is Instant preferred over java.util.Date in an entity? (immutable,
 *     thread-safe, nanosecond precision, no hidden default time zone)
 *   - What breaks if you store LocalDateTime for an event that spans zones?
 *   - How do you handle a DST gap, e.g. 02:30 on a spring-forward day?
 *
 * RUN
 *   main() runs 4 cases (typical +0530, negative offset, equal-instant check,
 *   and the missing-offset failure) and prints actual vs expected.
 */
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

class StringZonedDateTimeToInstant {

    /** Mirrors "2025-11-12 23:31:00.000 +0530"; Z is the RFC-822 style offset. */
    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS Z");

    /** Text with an offset -> the UTC point in time it names. */
    static Instant toInstant(String text) {
        OffsetDateTime offsetDateTime = OffsetDateTime.parse(text, FORMAT);
        return offsetDateTime.toInstant();   // subtracts the offset, result is UTC
    }

    /** Returns the exception type name instead of blowing up, so main can assert on it. */
    static String toInstantOrError(String text) {
        try {
            return toInstant(text).toString();
        } catch (DateTimeParseException e) {
            return "DateTimeParseException";
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Typical: India offset, so UTC is 5h30m earlier.
        print("+0530                   : ", toInstant("2025-11-12 23:31:00.000 +0530"),
                "2025-11-12T18:01:00Z");

        // Edge: negative offset pushes UTC past midnight into the next day.
        print("-0800 (rolls the date)  : ", toInstant("2025-11-12 23:31:00.000 -0800"),
                "2025-11-13T07:31:00Z");

        // Tricky: different text, different offset, same point in time.
        Instant india = toInstant("2025-11-12 23:31:00.000 +0530");
        Instant utc = toInstant("2025-11-12 18:01:00.000 +0000");
        print("same instant?           : ", india.equals(utc), true);

        // Edge: no offset in the text, so OffsetDateTime has nothing to anchor to.
        print("no offset in text       : ", toInstantOrError("2025-11-12 23:31:00.000"),
                "DateTimeParseException");
    }
}
