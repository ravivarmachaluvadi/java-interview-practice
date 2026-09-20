/*
 * =====================================================================
 *  Longest Subarray With Sum K                 LeetCode 325 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array nums (negatives allowed) and a target k, return the length of the
 *   longest contiguous subarray whose elements sum to exactly k, or 0 if none exists.
 *   The general answer must handle negatives; the O(1)-space window only works without them.
 *
 * EXAMPLE
 *   nums = [1, 2, 3, 7, 5], k = 12     ->  3   ([2,3,7])
 *   nums = [1, -1, 5, -2, 3], k = 3    ->  4   ([1,-1,5,-2]); the sliding window returns 0 here
 *   nums = [2, 0, 0, 3], k = 3         ->  3   ([0,0,3]); needs the FIRST index of prefix 2
 *   nums = [0, 0, 0], k = 0            ->  3   whole array; caught by prefixSum == k
 *   nums = [1, 2], k = 10              ->  0   no match
 *
 * APPROACH  (prefix sum + first-index map)
 *   1. Keep running prefixSum. If prefixSum == k the whole prefix [0..i] qualifies: length i+1.
 *   2. Otherwise, if an earlier prefix j had sum prefixSum - k, then (j..i] sums to k, length
 *      i - j. Look j up in a map prefixSum -> first index where it was seen.
 *   3. Store prefixSum with putIfAbsent so the EARLIEST index is kept: the longest subarray
 *      ending at i comes from the furthest-back matching prefix.
 *
 * APPROACH 2  (sliding window, non-negative arrays only)
 *   1. Expand right; while windowSum > k shrink from the left ("while", not "if").
 *   2. If windowSum == k, record the window length. Fails with negatives because shrinking no
 *      longer guarantees the sum goes down, so a valid longer window can be skipped.
 *
 * KEY INSIGHT
 *   This is the sibling of LC 560 and the contrast decides the whole family: COUNT problems
 *   store a count per prefix (put / merge); LONGEST problems store the first index per prefix
 *   (putIfAbsent). Overwriting the index when a zero-sum stretch repeats a prefix would
 *   shorten the answer. State the constraint out loud: "negatives?" -> map; "non-negative" ->
 *   window is also fine.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass; O(1) map work per element
 *   Space O(n)  map of distinct prefix sums  (window version: O(1))
 *
 * INTERVIEW FOLLOW-UPS
 *   - Shortest subarray with sum k with negatives (LC 862): monotonic deque on prefix sums.
 *   - Count instead of longest (LC 560): swap putIfAbsent for a count map seeded with {0: 1}.
 *   - Longest subarray with equal 0s and 1s (LC 525): map 0 -> -1 and this is sum k = 0.
 *   - Why long for the prefix sum? 10^5 elements of 10^9 overflows int.
 *
 * RUN
 *   main() runs 5 cases (typical, negatives, repeated prefix, all zeros, no match) through the
 *   map method, the window method on non-negative inputs, and one deliberate demonstration of
 *   the window failing on negatives. Prints actual vs expected.
 */
import java.util.HashMap;
import java.util.Map;

class ImportantLongestSubarrayWithSumKHash {

    /** Approach 1: prefix sum + map of the FIRST index at which each prefix sum was seen. */
    public static int longestWithPrefixSumHash(int[] nums, long k) {
        Map<Long, Integer> firstIndexOfPrefixSum = new HashMap<>();
        long prefixSum = 0;               // long: a sum of many ints can overflow int
        int maxLength = 0;

        for (int i = 0; i < nums.length; i++) {
            prefixSum += nums[i];

            // whole prefix [0..i] sums to k
            if (prefixSum == k) maxLength = i + 1;

            // if some earlier prefix j had sum (prefixSum - k), then (j..i] sums to k
            long neededEarlierPrefix = prefixSum - k;
            Integer j = firstIndexOfPrefixSum.get(neededEarlierPrefix);
            if (j != null) maxLength = Math.max(maxLength, i - j);

            // putIfAbsent, NOT put: keep the earliest index so the subarray is as long as possible
            // (with put you would overwrite it when a zero-sum stretch repeats a prefix)
            firstIndexOfPrefixSum.putIfAbsent(prefixSum, i);
        }
        return maxLength;
    }

    /** Approach 2: sliding window. Correct only for non-negative elements. */
    public static int longestWithSlidingWindow(int[] nums, int k) {
        int left = 0, windowSum = 0, maxLength = 0;

        for (int right = 0; right < nums.length; right++) {
            windowSum += nums[right];

            // "while", not "if": a single element may push the sum far past k
            while (windowSum > k && left <= right) {
                windowSum -= nums[left];
                left++;
            }

            if (windowSum == k) maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }

    private static boolean hasNegative(int[] nums) {
        for (int x : nums) if (x < 0) return true;
        return false;
    }

    private static void run(String label, int[] nums, int k, int expected) {
        System.out.println(label + " map    : " + longestWithPrefixSumHash(nums, k) + "   expected " + expected);
        if (!hasNegative(nums)) {
            System.out.println(label + " window : " + longestWithSlidingWindow(nums, k) + "   expected " + expected);
        }
    }

    public static void main(String[] args) {
        run("case 1 (typical)", new int[]{1, 2, 3, 7, 5}, 12, 3);
        run("case 2 (negatives)", new int[]{1, -1, 5, -2, 3}, 3, 4);
        run("case 3 (repeated prefix, putIfAbsent)", new int[]{2, 0, 0, 3}, 3, 3);
        run("case 4 (all zeros)", new int[]{0, 0, 0}, 0, 3);
        run("case 5 (no match)", new int[]{1, 2}, 10, 0);

        // Deliberate demonstration: the window is WRONG on negatives (true answer is 4).
        System.out.println("demo window on negatives: " + longestWithSlidingWindow(new int[]{1, -1, 5, -2, 3}, 3)
                + "   expected 0 (wrong on purpose; true answer 4, window cannot handle negatives)");
    }
}
