/*
 * =====================================================================
 *  Strobogrammatic Number                              LeetCode 246 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given a string of digits, return true if it reads the same when the whole
 *   string is rotated 180 degrees. Only 0, 1, 8 rotate to themselves; 6 and 9
 *   rotate into each other; 2, 3, 4, 5, 7 have no valid rotation at all.
 *
 * EXAMPLE
 *   "69"   ->  true    6 becomes 9 and 9 becomes 6
 *   "818"  ->  true    middle 1 must map to itself
 *   "962"  ->  false   2 has no rotation
 *   "2"    ->  false   single digit that does not self-rotate
 *   "1"    ->  true    single self-rotating digit
 *   ""     ->  true    empty string, loop never runs
 *
 * APPROACH  (two pointers with a mapping table)
 *   1. Build a table: 0->0, 1->1, 6->9, 8->8, 9->6.
 *   2. Put left at index 0 and right at the last index.
 *   3. While left <= right: the left digit must be in the table, and its
 *      rotated value must equal the right digit; otherwise return false.
 *   4. Move both pointers inward. If the loop finishes, return true.
 *   Note the loop uses <= so a middle digit (odd length) is checked against
 *   itself, which correctly rejects "6" and accepts "8".
 *
 * KEY INSIGHT
 *   This is the palindrome two-pointer scan with equality replaced by a lookup:
 *   instead of s[left] == s[right], we need rotate(s[left]) == s[right].
 *   Any "mirror" check with a symbol substitution has this same shape.
 *
 * COMPLEXITY
 *   Time  O(n)  each digit is examined once
 *   Space O(1)  the table has five fixed entries
 *
 * INTERVIEW FOLLOW-UPS
 *   - Generate all strobogrammatic numbers of length n (LeetCode 247): recurse
 *     from the outside in, adding a pair at both ends, and avoid a leading 0.
 *   - Count strobogrammatic numbers in a range [low, high] (LeetCode 248).
 *   - Why check the middle digit? "6" is not strobogrammatic, "8" is.
 *
 * RUN
 *   main() runs 6 cases (typical, edge, tricky) and prints actual vs expected.
 */
import java.util.HashMap;
import java.util.Map;

class StrobogrammaticNumber {

    /** Each digit mapped to what it looks like after a 180-degree rotation. */
    private static final Map<Character, Character> ROTATED = new HashMap<>();

    static {
        ROTATED.put('0', '0');
        ROTATED.put('1', '1');
        ROTATED.put('6', '9');
        ROTATED.put('8', '8');
        ROTATED.put('9', '6');
    }

    public static boolean isStrobogrammatic(String num) {
        int left = 0;
        int right = num.length() - 1;

        // <= so an odd-length middle digit is compared with itself
        while (left <= right) {
            char cLeft = num.charAt(left);
            char cRight = num.charAt(right);

            // left digit must have a rotation, and that rotation must be the right digit
            if (!ROTATED.containsKey(cLeft) || ROTATED.get(cLeft) != cRight) {
                return false;
            }
            left++;
            right--;
        }
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 \"69\"", isStrobogrammatic("69"), true);
        print("case 2 \"818\"", isStrobogrammatic("818"), true);
        print("case 3 \"962\"", isStrobogrammatic("962"), false);
        print("case 4 \"2\" (single, no rotation)", isStrobogrammatic("2"), false);
        print("case 5 \"1\" (single, self-rotating)", isStrobogrammatic("1"), true);
        print("case 6 \"\" (empty)", isStrobogrammatic(""), true);
    }
}
