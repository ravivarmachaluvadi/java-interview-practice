/*
 * =====================================================================
 *  P002 Prefix Sum + HashMap   Canonical LC 560 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 560, Subarray Sum Equals K)
 *   Given an int array nums (negatives allowed) and an int k, return how many
 *   contiguous non-empty subarrays sum to exactly k.
 *
 * EXAMPLE
 *   [1, 1, 1],  k = 2  ->  2
 *   [1, 2, 3],  k = 3  ->  2      [1, 2] and [3]
 *   [1, -1, 0], k = 0  ->  3      the trap: a sliding window cannot see these
 *
 * RECOGNIZE WHEN
 *   - "number of subarrays" or "longest subarray" with sum (or balance, or remainder) == k.
 *   - Values can be negative or zero, so growing a window does not grow the sum.
 *   - "equal number of 0s and 1s" / "divisible by k": both rewrite into a prefix condition.
 *   Not this if: all values are positive and you want longest/shortest -> sliding window
 *   (P017_VariableWindowLongest, P018_VariableWindowShortest), which is O(1) space.
 *
 * TEMPLATE
 *   seen = {0: 1}            // count version; longest version stores {0: -1} = first index
 *   sum = 0
 *   for i, x in nums:
 *       sum += x
 *       answer += seen.get(sum - k, 0)       // longest: i - firstIndex[sum - k]
 *       seen[sum] += 1                        // longest: putIfAbsent(sum, i)
 *
 * APPROACH
 *   1. sum(i+1..j) = prefix[j] - prefix[i]; we want that to equal k.
 *   2. So at index j, every earlier prefix equal to prefix[j] - k ends one valid subarray.
 *   3. Keep a map prefix value -> how many times seen; seed it with {0: 1} for the empty
 *      prefix so subarrays starting at index 0 are counted.
 *
 * KEY INSIGHT
 *   "Subarray sums to k" is "two prefix sums differ by k", so the question becomes a
 *   two-sum lookup on prefix sums. It works with negatives because it never moves a
 *   window; it only remembers what it has seen.
 *
 * COMPLEXITY
 *   Time O(n), space O(n) for the map (O(k) when the key is a remainder mod k).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 525  Contiguous Array           0 -> -1, longest subarray with sum 0
 *   [coded] LC 974  Subarray Sums Divisible K  key = ((sum % k) + k) % k, count pairs
 *   [coded] LC 523  Continuous Subarray Sum    key = sum % k, first index, length >= 2
 *   [coded] LC 325  Max Size Subarray Sum = K  store FIRST index of each prefix
 *           LC 930  Binary Subarrays With Sum  same as 560 (or P019_CountSubarraysAtMost)
 *           LC 1248 Count Nice Subarrays       odd -> 1, even -> 0, then LC 560
 *           LC 1074 Submatrices Sum to Target  fix a pair of rows, LC 560 on column sums
 *           LC 1590 Make Sum Divisible by P    shortest subarray with sum % p == total % p
 *           LC 437  Path Sum III               same map on root-to-node path
 *                                              -> P054_TopDownDfsPath
 *
 * PITFALLS
 *   - Forgetting the seed {0: 1} (or {0: -1}) loses every subarray that starts at 0.
 *   - Longest: keep the FIRST index (putIfAbsent); overwriting makes subarrays shorter.
 *   - Count: look up sum - k BEFORE adding the current sum, or k == 0 counts empties.
 *   - Java % is negative for negative sums; normalise with ((s % k) + k) % k.
 *
 * DEEP DIVE
 *   C01_CountSubarraySumEqualsK, C03_LongestSubarrayWithSumKHash (05-Hashing-Prefix-Sum)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.HashMap;
import java.util.Map;

class PrefixSumHashMap {

    // Canonical LC 560: count subarrays whose sum is exactly k.
    static int subarraySum(int[] nums, int k) {
        Map<Long, Integer> seen = new HashMap<>();
        seen.put(0L, 1);                      // the empty prefix
        long sum = 0;
        int count = 0;
        for (int x : nums) {
            sum += x;
            count += seen.getOrDefault(sum - k, 0);
            seen.merge(sum, 1, Integer::sum);
        }
        return count;
    }

    // LC 325: longest subarray with sum k. Same lookup, but remember the FIRST index.
    static int maxSubArrayLen(int[] nums, int k) {
        Map<Long, Integer> first = new HashMap<>();
        first.put(0L, -1);
        long sum = 0;
        int best = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            Integer start = first.get(sum - k);
            if (start != null) {
                best = Math.max(best, i - start);
            }
            first.putIfAbsent(sum, i);
        }
        return best;
    }

    // LC 525: equal 0s and 1s == sum 0 once every 0 counts as -1. Then it is LC 325, k = 0.
    static int findMaxLength(int[] nums) {
        Map<Integer, Integer> first = new HashMap<>();
        first.put(0, -1);
        int balance = 0;
        int best = 0;
        for (int i = 0; i < nums.length; i++) {
            balance += nums[i] == 1 ? 1 : -1;
            Integer start = first.get(balance);
            if (start != null) {
                best = Math.max(best, i - start);
            } else {
                first.put(balance, i);
            }
        }
        return best;
    }

    // LC 974: two prefixes with the same remainder bound a subarray divisible by k.
    static int subarraysDivByK(int[] nums, int k) {
        int[] remainderCount = new int[k];
        remainderCount[0] = 1;
        int sum = 0;
        int count = 0;
        for (int x : nums) {
            sum = ((sum + x) % k + k) % k;   // keep it in 0..k-1 even for negatives
            count += remainderCount[sum];
            remainderCount[sum]++;
        }
        return count;
    }

    // LC 523: same remainder idea, but the subarray must have length >= 2, so keep the
    // first index of each remainder and compare distances.
    static boolean checkSubarraySum(int[] nums, int k) {
        Map<Integer, Integer> first = new HashMap<>();
        first.put(0, -1);
        int sum = 0;
        for (int i = 0; i < nums.length; i++) {
            sum = (sum + nums[i]) % k;
            Integer start = first.get(sum);
            if (start != null) {
                if (i - start >= 2) {
                    return true;
                }
            } else {
                first.put(sum, i);
            }
        }
        return false;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 560 [1,1,1] k=2", subarraySum(new int[]{1, 1, 1}, 2), 2);
        check("LC 560 [1,2,3] k=3", subarraySum(new int[]{1, 2, 3}, 3), 2);
        check("LC 560 [1,-1,0] k=0 negatives", subarraySum(new int[]{1, -1, 0}, 0), 3);
        check("LC 560 [3,4,7,2,-3,1,4,2] k=7",
                subarraySum(new int[]{3, 4, 7, 2, -3, 1, 4, 2}, 7), 4);

        check("LC 325 [1,-1,5,-2,3] k=3", maxSubArrayLen(new int[]{1, -1, 5, -2, 3}, 3), 4);
        check("LC 325 [-2,-1,2,1] k=1", maxSubArrayLen(new int[]{-2, -1, 2, 1}, 1), 2);
        check("LC 325 [1,2] k=10 none", maxSubArrayLen(new int[]{1, 2}, 10), 0);

        check("LC 525 [0,1]", findMaxLength(new int[]{0, 1}), 2);
        check("LC 525 [0,1,0]", findMaxLength(new int[]{0, 1, 0}), 2);
        check("LC 525 [0,1,1,1,1,1,0,0,0]", findMaxLength(new int[]{0, 1, 1, 1, 1, 1, 0, 0, 0}), 6);

        check("LC 974 [4,5,0,-2,-3,1] k=5", subarraysDivByK(new int[]{4, 5, 0, -2, -3, 1}, 5), 7);
        check("LC 974 [5] k=9", subarraysDivByK(new int[]{5}, 9), 0);
        check("LC 974 [-1,2,9] k=2 negative sum", subarraysDivByK(new int[]{-1, 2, 9}, 2), 2);

        check("LC 523 [23,2,4,6,7] k=6", checkSubarraySum(new int[]{23, 2, 4, 6, 7}, 6), true);
        check("LC 523 [23,2,6,4,7] k=13", checkSubarraySum(new int[]{23, 2, 6, 4, 7}, 13), false);
        check("LC 523 [0] k=1 too short", checkSubarraySum(new int[]{0}, 1), false);
        check("LC 523 [5,0,0,0] k=3", checkSubarraySum(new int[]{5, 0, 0, 0}, 3), true);
    }
}
