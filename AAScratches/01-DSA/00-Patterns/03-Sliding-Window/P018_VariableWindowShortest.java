/*
 * =====================================================================
 *  P018 Variable Sliding Window: Shortest Valid   Canonical LC 209 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 209, Minimum Size Subarray Sum)
 *   nums holds POSITIVE integers. Return the length of the shortest contiguous subarray
 *   whose sum is >= target, or 0 if none exists.
 *
 * EXAMPLE
 *   target 7,  [2, 3, 1, 2, 4, 3]         ->  2     [4, 3]
 *   target 4,  [1, 4, 4]                  ->  1
 *   target 11, [1, 1, 1, 1, 1, 1, 1, 1]   ->  0     never reached
 *
 * RECOGNIZE WHEN
 *   - "minimum length / smallest window such that <condition>".
 *   - Once a window is valid, every LARGER window containing it is valid too
 *     (sum >= target with positives, "contains all of t", "outside part is balanced").
 *   Not this if: you want the longest -> P017_VariableWindowLongest; values can be negative
 *   -> LC 862 needs prefix sums + a monotonic deque (P042_MonotonicDeque).
 *
 * TEMPLATE
 *   left = 0, best = infinity
 *   for right in 0..n-1:
 *       add a[right]
 *       while window is valid:               // shrink WHILE valid, recording as you go
 *           best = min(best, right - left + 1)
 *           remove a[left]; left++
 *   return best == infinity ? 0 : best
 *
 * APPROACH
 *   1. Grow right until the sum reaches target.
 *   2. Then shrink from the left as long as the sum stays >= target, recording each length.
 *
 * KEY INSIGHT
 *   This mirrors P017_VariableWindowLongest: there you shrink while INVALID and record
 *   after the loop; here you shrink while VALID and record inside it. Which side the
 *   record sits on is the whole difference between "longest" and "shortest".
 *
 * COMPLEXITY
 *   Time O(n) (left and right each move n times), space O(1) or O(alphabet).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 76   Minimum Window Substring valid when every char of t is covered; keep a
 *                                            "chars still missing" counter
 *   [coded] LC 1234 Replace for Balanced     the window is what you replace; valid when every
 *                                            count OUTSIDE it is <= n / 4
 *           LC 2875 Min Size Subarray Infinite  whole copies of the array, then LC 209 on two
 *                                            concatenated copies for the remainder
 *           LC 1658 Min Ops to Reduce X to 0 longest middle window with sum total - x
 *                                            (longest-window shape)
 *           LC 862  Shortest Subarray Sum >= K  negatives allowed -> P042_MonotonicDeque
 *
 * PITFALLS
 *   - Use "while", not "if": one new element may let you drop several from the left.
 *   - Return 0 (or "") when nothing was ever valid; do not return the sentinel.
 *   - LC 76: compare counts with the NEED map, not just presence; t may repeat letters.
 *
 * DEEP DIVE
 *   C11_SmallestSubarraySum (01-Arrays),
 *   D02_MinimumWindowSubstring (02-Two-Pointers-Sliding-Window)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class VariableWindowShortest {

    // Canonical LC 209.
    static int minSubArrayLen(int target, int[] nums) {
        int left = 0;
        int sum = 0;
        int best = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum >= target) {
                best = Math.min(best, right - left + 1);
                sum -= nums[left++];
            }
        }
        return best == Integer.MAX_VALUE ? 0 : best;
    }

    // LC 76: shortest substring of s containing every character of t (with counts).
    static String minWindow(String s, String t) {
        int[] need = new int[128];
        for (char c : t.toCharArray()) {
            need[c]++;
        }
        int missing = t.length();                 // characters of t not yet covered
        int left = 0;
        int bestStart = 0;
        int bestLen = Integer.MAX_VALUE;
        for (int right = 0; right < s.length(); right++) {
            if (need[s.charAt(right)]-- > 0) {
                missing--;                        // this char was still needed
            }
            while (missing == 0) {
                if (right - left + 1 < bestLen) {
                    bestLen = right - left + 1;
                    bestStart = left;
                }
                if (++need[s.charAt(left++)] > 0) {
                    missing++;                    // dropped a char we needed
                }
            }
        }
        return bestLen == Integer.MAX_VALUE ? "" : s.substring(bestStart, bestStart + bestLen);
    }

    // LC 1234: s has only Q, W, E, R and length divisible by 4.
    static int balancedString(String s) {
        int n = s.length();
        int[] outside = new int[128];
        for (char c : s.toCharArray()) {
            outside[c]++;                         // initially the window is empty
        }
        int quota = n / 4;
        if (fits(outside, quota)) {
            return 0;
        }
        int left = 0;
        int best = n;
        for (int right = 0; right < n; right++) {
            outside[s.charAt(right)]--;           // char moves into the window
            while (left <= right && fits(outside, quota)) {
                best = Math.min(best, right - left + 1);
                outside[s.charAt(left++)]++;
            }
        }
        return best;
    }

    private static boolean fits(int[] outside, int quota) {
        return outside['Q'] <= quota && outside['W'] <= quota
                && outside['E'] <= quota && outside['R'] <= quota;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 209 t=7", minSubArrayLen(7, new int[]{2, 3, 1, 2, 4, 3}), 2);
        check("LC 209 t=4", minSubArrayLen(4, new int[]{1, 4, 4}), 1);
        check("LC 209 t=11 impossible", minSubArrayLen(11, new int[]{1, 1, 1, 1, 1, 1, 1, 1}), 0);
        check("LC 209 t=15 whole array", minSubArrayLen(15, new int[]{1, 2, 3, 4, 5}), 5);

        check("LC 76 ADOBECODEBANC / ABC", minWindow("ADOBECODEBANC", "ABC"), "BANC");
        check("LC 76 a / a", minWindow("a", "a"), "a");
        check("LC 76 a / aa needs two", "[" + minWindow("a", "aa") + "]", "[]");

        check("LC 1234 QWER", balancedString("QWER"), 0);
        check("LC 1234 QQWE", balancedString("QQWE"), 1);
        check("LC 1234 QQQW", balancedString("QQQW"), 2);
        check("LC 1234 QQQQ", balancedString("QQQQ"), 3);
    }
}
