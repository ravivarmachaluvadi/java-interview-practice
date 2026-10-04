/*
 * =====================================================================
 *  P019 Sliding Window: Count Subarrays (At-Most Trick)   Canonical LC 713 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 713, Subarray Product Less Than K)
 *   nums holds positive integers. Count the contiguous subarrays whose product is
 *   strictly less than k.
 *
 * EXAMPLE
 *   [10, 5, 2, 6], k = 100  ->  8   [10] [5] [2] [6] [10,5] [5,2] [2,6] [5,2,6]
 *   [1, 2, 3],     k = 0    ->  0   no product of positives is < 0
 *
 * RECOGNIZE WHEN
 *   - "number of subarrays / substrings such that ..." with a monotonic condition.
 *   - "EXACTLY k distinct / k odd numbers / sum k" on non-negative input: write it as
 *     atMost(k) - atMost(k - 1).
 *   Not this if: values can be negative with a sum condition -> P002_PrefixSumHashMap;
 *   you want the length of one best window -> P017_VariableWindowLongest.
 *
 * TEMPLATE
 *   atMost(k):
 *       left = 0, count = 0
 *       for right in 0..n-1:
 *           add a[right]
 *           while window breaks the "at most k" rule: remove a[left]; left++
 *           count += right - left + 1         // valid subarrays that END at right
 *   exactly(k) = atMost(k) - atMost(k - 1)
 *
 * APPROACH
 *   1. Keep the largest valid window ending at right (shrink while product >= k).
 *   2. Every subarray [i, right] with left <= i <= right is valid too (shorter, smaller
 *      product), so right - left + 1 new subarrays are counted at once.
 *
 * KEY INSIGHT
 *   Count subarrays by their RIGHT end: with monotonic validity, the valid starts for a
 *   given end form one contiguous range [left, right]. "Exactly k" is not monotonic, but
 *   the difference of two "at most" counts is, which turns it into two plain windows.
 *
 * COMPLEXITY
 *   Time O(n) per atMost call, space O(1) or O(distinct values).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 992  Subarrays K Different    atMost(k) - atMost(k - 1), map of counts
 *   [coded] LC 1248 Count Nice Subarrays     odd count: atMost(k) - atMost(k - 1)
 *   [coded] LC 930  Binary Subarrays Sum     sum of 0/1 values: atMost(goal) - atMost(goal - 1)
 *           LC 1358 Substrings With a, b, c  count windows ending at right that contain all
 *                                            three: add left (the number of valid starts)
 *           LC 2302 Count Subarrays Score < K  shrink while sum * len >= k
 *           LC 2962 Max Element >= K Times   count starts once the window holds k copies
 *           LC 795  Subarrays With Bounded Max  atMost(right) - atMost(left - 1) on the max
 *
 * PITFALLS
 *   - LC 713 with k <= 1: no positive product is < k; guard, or the while loop empties
 *     the window past right.
 *   - atMost(k - 1) when k = 0: return 0 for a negative bound.
 *   - The count can reach n * (n + 1) / 2; use long for big n.
 *
 * DEEP DIVE
 *   C08_SubarrayProductLessThanK (02-Two-Pointers-Sliding-Window),
 *   C02_BinarySubarraysWithSum (05-Hashing-Prefix-Sum)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.HashMap;
import java.util.Map;

class CountSubarraysAtMost {

    // Canonical LC 713.
    static int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1) {
            return 0;
        }
        long product = 1;
        int left = 0;
        int count = 0;
        for (int right = 0; right < nums.length; right++) {
            product *= nums[right];
            while (product >= k) {
                product /= nums[left++];
            }
            count += right - left + 1;
        }
        return count;
    }

    // LC 992: subarrays with exactly k distinct values.
    static int subarraysWithKDistinct(int[] nums, int k) {
        return atMostKDistinct(nums, k) - atMostKDistinct(nums, k - 1);
    }

    private static int atMostKDistinct(int[] nums, int k) {
        if (k < 0) {
            return 0;
        }
        Map<Integer, Integer> count = new HashMap<>();
        int left = 0;
        int total = 0;
        for (int right = 0; right < nums.length; right++) {
            count.merge(nums[right], 1, Integer::sum);
            while (count.size() > k) {
                int out = nums[left++];
                if (count.merge(out, -1, Integer::sum) == 0) {
                    count.remove(out);
                }
            }
            total += right - left + 1;
        }
        return total;
    }

    // LC 1248: exactly k odd numbers. Odd -> 1, even -> 0, then "sum at most k".
    static int numberOfSubarrays(int[] nums, int k) {
        return atMostSum(nums, k, true) - atMostSum(nums, k - 1, true);
    }

    // LC 930: binary array, exactly `goal` ones.
    static int numSubarraysWithSum(int[] nums, int goal) {
        return atMostSum(nums, goal, false) - atMostSum(nums, goal - 1, false);
    }

    // Subarrays whose sum of weights is <= limit; weight = value (0/1) or (value is odd).
    private static int atMostSum(int[] nums, int limit, boolean oddAsOne) {
        if (limit < 0) {
            return 0;
        }
        int left = 0;
        int sum = 0;
        int total = 0;
        for (int right = 0; right < nums.length; right++) {
            sum += weight(nums[right], oddAsOne);
            while (sum > limit) {
                sum -= weight(nums[left++], oddAsOne);
            }
            total += right - left + 1;
        }
        return total;
    }

    private static int weight(int x, boolean oddAsOne) {
        return oddAsOne ? (x & 1) : x;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 713 [10,5,2,6] k=100",
                numSubarrayProductLessThanK(new int[]{10, 5, 2, 6}, 100), 8);
        check("LC 713 [1,2,3] k=0", numSubarrayProductLessThanK(new int[]{1, 2, 3}, 0), 0);
        check("LC 713 [1,1,1] k=2", numSubarrayProductLessThanK(new int[]{1, 1, 1}, 2), 6);

        check("LC 992 [1,2,1,2,3] k=2", subarraysWithKDistinct(new int[]{1, 2, 1, 2, 3}, 2), 7);
        check("LC 992 [1,2,1,3,4] k=3", subarraysWithKDistinct(new int[]{1, 2, 1, 3, 4}, 3), 3);

        check("LC 1248 [1,1,2,1,1] k=3", numberOfSubarrays(new int[]{1, 1, 2, 1, 1}, 3), 2);
        check("LC 1248 [2,4,6] k=1", numberOfSubarrays(new int[]{2, 4, 6}, 1), 0);
        check("LC 1248 [2,2,2,1,2,2,1,2,2,2] k=2",
                numberOfSubarrays(new int[]{2, 2, 2, 1, 2, 2, 1, 2, 2, 2}, 2), 16);

        check("LC 930 [1,0,1,0,1] goal=2", numSubarraysWithSum(new int[]{1, 0, 1, 0, 1}, 2), 4);
        check("LC 930 five zeros goal=0", numSubarraysWithSum(new int[]{0, 0, 0, 0, 0}, 0), 15);
    }
}
