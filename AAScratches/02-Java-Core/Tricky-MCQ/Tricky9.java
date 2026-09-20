/*
 * =====================================================================
 *  Overload resolution when the argument is null      Tricky MCQ | Medium
 * =====================================================================
 *
 * QUESTION
 *   A class has two overloads of m1: one taking String, one taking Object.
 *   Each returns a tag naming itself. What does m1(null) return, and does
 *   the call even compile when a third overload taking StringBuilder exists?
 *
 *     String m1(String s)  { return "string"; }
 *     String m1(Object s)  { return "object"; }
 *
 *     sc.m1(null);   // <- which one?
 *
 * OPTIONS
 *   A) "object"  - null is typeless, so it falls back to Object
 *   B) "string"  - the most specific applicable overload wins
 *   C) it does not compile - the call is ambiguous
 *   D) it compiles but throws NullPointerException at runtime
 *
 * RUN
 *   main() runs 4 cases and prints actual vs expected:
 *     1. m1(null)           - the puzzle itself
 *     2. m1((Object) null)  - a cast steers resolution to the other overload
 *     3. String + StringBuilder overloads - the genuinely ambiguous shape,
 *        which only compiles once a cast picks a winner (cases 3 and 4)
 *
 * ---------------------------------------------------------------------
 *  STOP HERE if you want to answer first. The answer follows.
 * ---------------------------------------------------------------------
 *
 * ANSWER
 *   B) "string".
 *
 * WHY
 *   null is assignable to every reference type, so both overloads are
 *   applicable. Java then picks the MOST SPECIFIC one: String is a subtype
 *   of Object, so every String argument is also a valid Object argument but
 *   not the reverse. String therefore wins, at compile time, with no runtime
 *   type check - the bytecode hard-codes the m1(String) call.
 *
 *   "Most specific" only decides between types on the same inheritance line.
 *   String and StringBuilder are siblings: neither is more specific than the
 *   other, so m1(null) with those two overloads is a compile error,
 *   "reference to m1 is ambiguous". A cast such as m1((String) null) gives
 *   the compiler a static type and resolves it.
 *
 * GOTCHAS
 *   - A widening primitive overload beats autoboxing, which beats varargs.
 *   - An untyped null passed to a varargs method is the array itself, not a
 *     one-element array; pass new Object[]{null} when you mean one element.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is overload resolution static while overriding is dynamic?
 *   - What happens with m1(int) and m1(Integer) when called with 5?
 *   - How do generics erase to Object and cause "same erasure" clashes?
 */
public class Tricky9 {

    /**
     * Each overload returns a tag naming itself, so main() can compare the
     * actual choice against the expected one on a single line.
     */
    String m1(String s) {
        return "string";
    }

    String m1(Object s) {
        return "object";
    }

    /**
     * Sibling overloads: String and StringBuilder are unrelated types, so a
     * bare m1(null) here is ambiguous and will not compile. Only an explicit
     * cast makes the call legal.
     */
    static class Ambiguous {
        String m1(String s) {
            return "string";
        }

        String m1(StringBuilder s) {
            return "builder";
        }
    }

    public static void main(String[] args) {
        Tricky9 sc = new Tricky9();

        // Case 1: the puzzle. String is more specific than Object, so it wins.
        System.out.println("case 1: m1(null)           -> " + sc.m1(null)
                + "   expected string");

        // Case 2: the cast gives null a static type, forcing the Object overload.
        System.out.println("case 2: m1((Object) null)  -> " + sc.m1((Object) null)
                + "   expected object");

        // Case 3: sibling overloads. Uncommenting the next line breaks the build
        // with "reference to m1 is ambiguous" - that is the whole point.
        //   new Ambiguous().m1(null);
        Ambiguous amb = new Ambiguous();
        System.out.println("case 3: ambiguous pair, m1((String) null) -> " + amb.m1((String) null)
                + "   expected string");
        System.out.println("case 4: ambiguous pair, m1((StringBuilder) null) -> "
                + amb.m1((StringBuilder) null) + "   expected builder");
    }
}
