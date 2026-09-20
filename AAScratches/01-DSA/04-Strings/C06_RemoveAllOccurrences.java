/*
 * =====================================================================
 *  Remove All Occurrences of a Substring          LeetCode 1910 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given strings s and part, repeatedly remove the LEFTMOST occurrence of part from
 *   s until part no longer appears, then return s. Removing one occurrence can create
 *   a new one (the pieces on either side join up), which is why it is repeated.
 *
 * EXAMPLE
 *   s = "daabcbaabcbc", part = "abc"  ->  "dab"   remove at 2, at 4, then at 3
 *   s = "axxxxyyyyb",   part = "xy"   ->  "ab"    each removal exposes the next "xy"
 *   s = "abc",          part = "abc"  ->  ""      whole string removed
 *   s = "hello",        part = "xyz"  ->  "hello" nothing to remove
 *
 * APPROACH  (Repeated indexOf and delete on a StringBuilder)
 *   1. Copy s into a StringBuilder so deletions are in place.
 *   2. Find the first occurrence with indexOf(part).
 *   3. While found: delete(index, index + part.length()), then search again from
 *      the start (a new occurrence may have formed to the LEFT of the old index).
 *   4. Return the builder's contents.
 *
 * KEY INSIGHT
 *   The naive loop is correct because it always re-searches from index 0, so any
 *   occurrence created by the join is found. The follow-up everyone expects is the
 *   stack version: push chars one by one and whenever the top of the stack ends with
 *   part, pop it off; that is one pass and O(n * m).
 *
 * COMPLEXITY
 *   Time  O(n^2)  up to n/m deletions, each indexOf and delete costs O(n)
 *   Space O(n)  the StringBuilder copy
 *
 * INTERVIEW FOLLOW-UPS
 *   - Stack / StringBuilder-as-stack version: append each char, and if the last
 *     part.length() chars equal part, setLength back; O(n * m) in one pass.
 *   - Why not s.replace(part, "") once? It does not re-scan the joined pieces, so
 *     "axxxxyyyyb" would become "axxxyyyb", not "ab".
 *   - Remove occurrences of several patterns: same stack, check each pattern at the top.
 *
 * RUN
 *   main() runs 4 cases (typical, cascading, whole string, no match) and prints
 *   actual vs expected for both the indexOf loop and the stack version.
 */
class RemoveAllOccurrences {

    /** Author's approach: repeatedly find the leftmost occurrence and delete it. */
    public static String removeOccurrences(String s, String part) {
        StringBuilder sb = new StringBuilder(s);
        int index = sb.indexOf(part);
        while (index != -1) {
            sb.delete(index, index + part.length()); // delete is [start, end)
            index = sb.indexOf(part);                // re-scan from 0: a new match may form left
        }
        return sb.toString();
    }

    /** One-pass stack version: build the answer and trim whenever it ends with part. */
    public static String removeOccurrencesStack(String s, String part) {
        StringBuilder stack = new StringBuilder();
        int m = part.length();
        for (char c : s.toCharArray()) {
            stack.append(c);
            if (stack.length() >= m && endsWith(stack, part)) {
                stack.setLength(stack.length() - m); // pop the matched suffix
            }
        }
        return stack.toString();
    }

    private static boolean endsWith(StringBuilder sb, String part) {
        int offset = sb.length() - part.length();
        for (int i = 0; i < part.length(); i++) {
            if (sb.charAt(offset + i) != part.charAt(i)) return false;
        }
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": \"" + actual + "\"   expected \"" + expected + "\"");
    }

    public static void main(String[] args) {
        String[][] cases = {
                {"daabcbaabcbc", "abc", "dab"},
                {"axxxxyyyyb", "xy", "ab"},
                {"abc", "abc", ""},
                {"hello", "xyz", "hello"},
        };
        for (int i = 0; i < cases.length; i++) {
            String s = cases[i][0], part = cases[i][1], expected = cases[i][2];
            print("case " + (i + 1) + " indexOf loop", removeOccurrences(s, part), expected);
            print("case " + (i + 1) + " stack       ", removeOccurrencesStack(s, part), expected);
        }
    }
}
