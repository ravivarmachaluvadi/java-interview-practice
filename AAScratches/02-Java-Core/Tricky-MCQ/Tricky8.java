/*
 * =====================================================================
 *  concat() without assigning the result                 Tricky MCQ | Easy
 * =====================================================================
 *
 * QUESTION
 *   String a = "abc";
 *   String b = "abc";
 *   a.concat(b);
 *   System.out.println(a);
 *
 *   What is printed?
 *
 * OPTIONS
 *   A. abcabc
 *   B. abc
 *   C. Compile error: result of concat() is unused
 *   D. null
 *
 * ---------------------------------------------------------------------
 *  ANSWER  ->  B.  It prints "abc".
 * ---------------------------------------------------------------------
 *
 * WHY
 *   1. String is IMMUTABLE. Its char data is final and can never change after
 *      construction, so concat() cannot modify a in place.
 *   2. concat() therefore builds and RETURNS a brand new String "abcabc".
 *   3. The statement "a.concat(b);" throws that return value away. The variable
 *      a still points at the original "abc" object, so that is what prints.
 *   4. Ignoring a return value is legal Java, so there is no compile error -
 *      only an IDE warning at most. Case 2 shows the fix: reassign the result.
 *
 * KEY INSIGHT
 *   Every String "mutator" - concat, substring, trim, replace, toUpperCase,
 *   strip - returns a NEW String and leaves the receiver untouched. If you do
 *   not assign the result, the call did nothing observable. Whenever you see a
 *   bare String method call on its own line, that is almost always the bug.
 *
 * GOTCHAS
 *   - StringBuilder.append() is the opposite: it mutates in place AND returns
 *     this, so ignoring its return value is harmless.
 *   - "abc" and "abc" are the same interned literal, so a == b is true here.
 *     a.concat(b) is computed at runtime, so its result is NOT interned and
 *     would fail an == comparison with the literal "abcabc" (case 3).
 *   - Concatenation in a loop creates a new object each pass; use a
 *     StringBuilder there.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is String immutable? (caching hash, string pool safety, thread
 *     safety, safe use as HashMap keys and in security checks)
 *   - "abc" + "abc" vs a.concat(b): which is folded by the compiler?
 *   - What does String.intern() do, and when would you call it?
 *   - When does the compiler rewrite + into StringBuilder or invokedynamic?
 *
 * RUN
 *   main() runs 3 cases: the discarded result, the assigned result, and the
 *   identity check that shows literals are pooled but runtime results are not.
 */
public class Tricky8 {

    public static void main(String[] args) {
        String a = "abc";
        String b = "abc";

        // Case 1 - the puzzle. The new String is created and immediately lost.
        a.concat(b);
        System.out.println("case 1 (result discarded): " + a + "        expected abc");

        // Case 2 - the fix: capture what concat() returns.
        String joined = a.concat(b);
        System.out.println("case 2 (result assigned):  " + joined + "     expected abcabc");

        // Case 3 - edge case on identity. Both literals are the same pooled
        // object, but a runtime-built String is a fresh object, so == is false
        // there even though equals() is true.
        System.out.println("case 3 (a == b literals):  " + (a == b) + "       expected true");
        System.out.println("case 3 (joined == \"abcabc\"): " + (joined == "abcabc")
                + "   expected false");
        System.out.println("case 3 (joined.equals):    " + joined.equals("abcabc")
                + "       expected true");
    }
}
