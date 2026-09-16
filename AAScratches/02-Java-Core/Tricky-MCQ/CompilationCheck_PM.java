/**
 * Formats an arbitrary object into a human‑readable string.
 *
 * The method distinguishes between Integer, Long, String, and null,
 * producing type‑specific prefixes; all other objects are rendered
 * via their {@code toString()} representation.
 *
 * Approach: A Java 17 switch expression with pattern matching is used
 * to dispatch on the runtime type of the argument. Each case formats
 * the value accordingly or falls back to {@code obj.toString()}.
 *
 * Time Complexity: O(1) – constant‑time type checking and formatting.
 * Space Complexity: O(1) – only a few temporary strings are created per call.
 */
public class PM {
    public static void main(String[] args) {
        System.out.println(format(new Object()));
        System.out.println(format( Integer.valueOf(12)));
        System.out.println(format( Double.valueOf(12)));
        System.out.println(format( Long.valueOf(12)));
        System.out.println(format( "String"));
    }

    static String format(Object obj) {
        return switch (obj) {
            case Integer i -> String.format("int %d", i);
            case Long l    -> String.format("long %d", l);
            case String s  -> String.format("String '%s'", s);
            case null      -> "null value";
            default        -> obj.toString();
        };
    }
}