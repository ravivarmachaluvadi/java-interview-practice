/*
 * =====================================================================
 *  Subarray Sum Equals K                       LeetCode 560 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array nums (values may be negative) and an int k, return the number of
 *   contiguous non-empty subarrays whose elements sum to exactly k. n can be 2 * 10^4, so an
 *   O(n^2) scan of every subarray is too slow; negatives rule out a sliding window.
 *
 * EXAMPLE
 *   nums = [1, 2, 3], k = 3    ->  2   ([1,2] and [3])
 *   nums = [1, -1, 0], k = 0   ->  3   ([1,-1], [0], [1,-1,0]); negatives break sliding window
 *   nums = [0, 0, 0], k = 0    ->  6   every one of the 3+2+1 subarrays sums to 0
 *   nums = [], k = 5           ->  0
 *
 * APPROACH  (prefix sum + count map)
 *   1. Keep a running prefix sum P(i) = nums[0] + ... + nums[i].
 *   2. The subarray (j, i] sums to k exactly when P(j) == P(i) - k.
 *   3. So at index i, look up how many earlier prefixes equal P(i) - k and add that to count.
 *   4. Then record P(i) in the map (count of times seen). Recording AFTER the lookup stops a
 *      prefix from matching itself.
 *   5. Seed the map with {0: 1}: the "empty prefix" has sum 0, which lets subarrays that start
 *      at index 0 be counted without a special case.
 *
 * KEY INSIGHT
 *   Turn "sum of a range" into "difference of two prefix sums", then the question becomes
 *   "how many earlier prefixes equal P(i) - k", which a HashMap answers in O(1). The seed
 *   {0: 1} is the detail people forget. Recognise the pattern: any "count subarrays with
 *   property X" where X depends on a running total is prefix sum + map.
 *
 * COMPLEXITY
 *   Time  O(n)  single pass, O(1) map lookup and insert per element
 *   Space O(n)  the map can hold one entry per distinct prefix sum
 *
 * INTERVIEW FOLLOW-UPS
 *   - Longest (not count) subarray with sum k: store the first index per prefix instead of a
 *     count and use putIfAbsent (see C03_LongestSubarrayWithSumKHash).
 *   - Subarray sum divisible by k (LC 974): key the map on prefixSum mod k, normalise negatives.
 *   - Binary array / all non-negative: sliding window works and needs O(1) space (see
 *   C02_BinarySubarraysWithSum).
 *   - Prefix sums can overflow int for large inputs; use long for the running sum.
 *
 * RUN
 *   main() runs 4 cases (typical, negatives, all zeros, empty) and prints actual vs expected.
 */
import java.util.HashMap;
import java.util.Map;

class ImportantCountSubarraySumEqualsK {

    public static int subarraySum(int[] nums, int k) {
        Map<Integer, Integer> prefixSumCount = new HashMap<>();
        prefixSumCount.put(0, 1); // empty prefix: makes subarrays starting at index 0 count
        int prefixSum = 0;
        int count = 0;

        for (int num : nums) {
            prefixSum += num;
            // every earlier prefix equal to (prefixSum - k) closes a subarray summing to k
            int neededEarlierPrefix = prefixSum - k;
            count += prefixSumCount.getOrDefault(neededEarlierPrefix, 0);
            // record the current prefix only after the lookup so it cannot match itself
            prefixSumCount.merge(prefixSum, 1, Integer::sum);
        }
        return count;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical)", subarraySum(new int[]{1, 2, 3}, 3), 2);
        print("case 2 (negatives)", subarraySum(new int[]{1, -1, 0}, 0), 3);
        print("case 3 (all zeros)", subarraySum(new int[]{0, 0, 0}, 0), 6);
        print("case 4 (empty)", subarraySum(new int[]{}, 5), 0);
    }
}
