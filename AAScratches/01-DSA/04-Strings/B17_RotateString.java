/*
 * =====================================================================
 *  Rotate String                                  LeetCode 796 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given strings s and goal, return true if goal can be obtained by rotating s
 *   some number of times. One rotation moves the first character of s to its end
 *   ("abcde" -> "bcdea").
 *
 * EXAMPLE
 *   s = "abcde", goal = "cdeab"  ->  true    two left rotations
 *   s = "abcde", goal = "abced"  ->  false   same letters, not a rotation
 *   s = "a",     goal = "a"      ->  true    zero rotations count
 *   s = "ab",    goal = "abc"    ->  false   length mismatch, decided up front
 *
 * APPROACH  (s + s contains goal)
 *   1. If the lengths differ, no rotation can work: return false.
 *   2. Build s + s. Every rotation of s appears as a contiguous substring of s + s
 *      (rotation by k is the window starting at index k).
 *   3. Return (s + s).contains(goal).
 *
 * KEY INSIGHT
 *   Doubling the string turns "is a rotation of" into "is a substring of". The
 *   length check is not optional: without it "" or a shorter goal would be found
 *   inside s + s. This doubling trick is reused in C06_GreatestCommonDivisorOfStrings.
 *
 * COMPLEXITY
 *   Time  O(n^2) worst case for String.contains (naive search); O(n) with KMP
 *   Space O(n)  for the doubled string
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it without extra space: try each shift k and compare s[(i+k) % n] to goal[i].
 *   - Make the search linear with KMP on the pattern goal over the text s + s.
 *   - Find the minimum number of rotations: (s + s).indexOf(goal).
 *
 * RUN
 *   main() runs 4 cases (typical, same letters, single char, length mismatch) and
 *   prints actual vs expected.
 */
class RotateString {

    public static boolean rotateString(String s, String goal) {
        if (s.length() != goal.length()) return false;
        return (s + s).contains(goal); // every rotation of s is a window of s + s
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical", rotateString("abcde", "cdeab"), true);
        print("case 2 same letters", rotateString("abcde", "abced"), false);
        print("case 3 single", rotateString("a", "a"), true);
        print("case 4 length mismatch", rotateString("ab", "abc"), false);
    }
}
