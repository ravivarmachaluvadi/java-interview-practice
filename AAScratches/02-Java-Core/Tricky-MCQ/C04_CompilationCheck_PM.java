/*
 * =====================================================================
 *  Pattern matching for switch: which case wins?          Tricky MCQ | Medium
 * =====================================================================
 *
 * QUESTION
 *   format() is a Java 21 switch expression with type patterns for Integer, Long,
 *   String and null, plus a default that calls obj.toString().
 *   What does each call below print, and does the file compile as written?
 *
 *     format(new Object())          -> ?
 *     format(Integer.valueOf(12))   -> ?
 *     format(Double.valueOf(12))    -> ?
 *     format(Long.valueOf(12))      -> ?
 *     format("String")              -> ?
 *     format(null)                  -> ?
 *
 * OPTIONS  (for the Double call)
 *   A. "int 12"        B. "long 12"        C. "12.0"        D. NullPointerException
 *
 * GOTCHAS
 *   - A switch over patterns with no "case null" throws NPE on null, it does NOT
 *     fall into default. "case null" is what makes null survivable here.
 *   - Boxed numeric types do not widen or convert in a type pattern: a Double is
 *     matched only by "case Double", never by "case Long" or "case Integer".
 *   - Fixed: the class was declared "public class PM" inside CompilationCheck_PM.java,
 *     which javac rejects. A public type must live in a file of its own name, so the
 *     class is now package-private and the file compiles.
 *
 * INTERVIEW FOLLOW-UPS
 *   - What happens if you delete "case null"? (NPE, thrown before any case is tried.)
 *   - Why must "default" come last, and when is a switch over patterns exhaustive?
 *   - How do guarded patterns ("case Integer i when i > 10") change the order of tests?
 *   - What does "case Integer i" compile down to, and how is it different from instanceof?
 *
 * RUN
 *   main() runs 6 cases (typical, the tricky Double, and the null edge) and prints
 *   actual vs expected. The Object case has its identity hash masked so it is stable.
 *
 * ---------------------------------------------------------------------
 * ANSWER  (stop above if you want to solve it yourself)
 * ---------------------------------------------------------------------
 *   Object -> "java.lang.Object@<hash>",  Integer -> "int 12",  Double -> "12.0" (C),
 *   Long -> "long 12",  "String" -> "String 'String'",  null -> "null value".
 *
 * WHY
 *   Cases are tested top to bottom on the RUNTIME type. Double matches no pattern, so
 *   it reaches default and prints Double.toString(12.0) = "12.0" - not "long 12", and
 *   not an error. null is caught only because "case null" is present.
 */
class PM {

    public static void main(String[] args) {
        print("case 1 (Object) ", maskHash(format(new Object())), "java.lang.Object@<hash>");
        print("case 2 (Integer)", format(Integer.valueOf(12)), "int 12");
        print("case 3 (Double) ", format(Double.valueOf(12)), "12.0");
        print("case 4 (Long)   ", format(Long.valueOf(12)), "long 12");
        print("case 5 (String) ", format("String"), "String 'String'");
        print("case 6 (null)   ", format(null), "null value");
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

    /** Object.toString() embeds an identity hash, so mask it to keep output stable. */
    static String maskHash(String s) {
        return s.replaceAll("@[0-9a-f]+$", "@<hash>");
    }

    static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
