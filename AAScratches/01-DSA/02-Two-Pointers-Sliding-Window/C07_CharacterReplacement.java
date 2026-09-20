/*
 * =====================================================================
 *  Longest Repeating Character Replacement          LeetCode 424 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an uppercase string s and an integer k, you may change at most k characters to
 *   any other uppercase letter. Return the length of the longest substring that can be
 *   made of one repeated letter after those changes.
 *
 * EXAMPLE
 *   s = "AABABBA", k = 1  ->  4   change the middle A: "AABBBBA" contains "BBBB"
 *   s = "ABAB",    k = 2  ->  4   change both A's (or both B's)
 *   s = "AAAA",    k = 0  ->  4   already uniform, nothing to change
 *   s = "",        k = 1  ->  0   empty input
 *   s = "XYZ",     k = 1  ->  2   one change turns any two neighbours into a pair
 *   s = "ZZZA",    k = 0  ->  3   'Z' is index 25 (the case the original code crashed on)
 *
 * APPROACH  (window minus max frequency <= k)
 *   1. Keep freq[26] for the letters inside [left, right] and maxFreq = the largest count
 *      seen in any window so far.
 *   2. Extend right: bump freq of the new letter and update maxFreq.
 *   3. A window is fixable when (windowLength - maxFreq) <= k: every letter that is not the
 *      majority letter must be replaced, and there are at most k of those.
 *   4. If fixable, record windowLength as a candidate.
 *      If not, move left forward by ONE (decrement its letter) and carry on; the window
 *      keeps its size and slides instead of shrinking.
 *
 * KEY INSIGHT
 *   maxFreq is deliberately never decreased when left moves. It is allowed to be stale
 *   because the answer only depends on the largest window that was ever valid, and a
 *   larger window can only become valid if maxFreq grows. So the window never needs to
 *   shrink below the best length found; it slides at that size until a better letter run
 *   pushes maxFreq up. This is the hardest member of the "at most k violations" family.
 *
 * Fixed: freq array was new int[25], so 'Z' - 'A' = 25 threw ArrayIndexOutOfBounds.
 *
 * COMPLEXITY
 *   Time  O(n)  right moves n times, left moves at most n times
 *   Space O(1)  fixed 26-slot array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is a stale maxFreq safe? (see KEY INSIGHT; be able to say it in two sentences)
 *   - Lowercase or mixed case input: use int[128] or a map, same logic.
 *   - Recompute maxFreq exactly on each shrink: correct but O(26 n); explain the trade-off.
 *   - Related: Max Consecutive Ones III (1004) is the same idea with k zeros allowed.
 *
 * RUN
 *   main() runs 6 cases (typical, edge, tricky) and prints actual vs expected.
 */
class CharacterReplacement {

    public static int characterReplacement(String s, int k) {
        int n = s.length();
        int[] freq = new int[26]; // 'A'..'Z' -> 26 slots (was 25: 'Z' crashed)
        int left = 0;
        int maxFreq = 0;   // largest single-letter count seen in any window so far (may be stale)
        int maxLength = 0;

        for (int right = 0; right < n; right++) {
            int idx = s.charAt(right) - 'A';
            freq[idx]++;
            maxFreq = Math.max(maxFreq, freq[idx]);

            int windowLength = right - left + 1;
            if (windowLength - maxFreq <= k) {
                // everything that is not the majority letter can be replaced within k changes
                maxLength = Math.max(maxLength, windowLength);
            } else {
                // too many non-majority letters: slide the window forward by one
                freq[s.charAt(left) - 'A']--;
                left++;
            }
        }
        return maxLength;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 AABABBA k=1", characterReplacement("AABABBA", 1), 4);
        print("case 2 ABAB k=2", characterReplacement("ABAB", 2), 4);
        print("case 3 AAAA k=0", characterReplacement("AAAA", 0), 4);
        print("case 4 empty k=1", characterReplacement("", 1), 0);
        print("case 5 XYZ k=1", characterReplacement("XYZ", 1), 2);
        print("case 6 ZZZA k=0", characterReplacement("ZZZA", 0), 3);
    }
}
