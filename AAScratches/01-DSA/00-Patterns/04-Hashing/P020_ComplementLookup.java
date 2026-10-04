/*
 * =====================================================================
 *  P020 HashMap: Complement Lookup   Canonical LC 1 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 1, Two Sum)
 *   Return the indices of the two numbers in nums that add up to target. Exactly one
 *   answer exists and the same element may not be used twice. nums is NOT sorted.
 *
 * EXAMPLE
 *   [2, 7, 11, 15], target 9  ->  [0, 1]
 *   [3, 2, 4],      target 6  ->  [1, 2]
 *   [3, 3],         target 6  ->  [0, 1]     the trap: equal values, different indices
 *
 * RECOGNIZE WHEN
 *   - "find a pair (i, j) with a[i] + a[j] == t" (or difference, or product, or
 *     (a[i] + a[j]) % k == 0) on UNSORTED input, often returning indices.
 *   - "is there a duplicate within distance k".
 *   - Split a 4-way condition into two halves: pair sums of A and B vs pair sums of C and D.
 *   Not this if: the input is sorted and O(1) space matters -> P011_OppositeEndsSorted; you
 *   need a contiguous range with sum k -> P002_PrefixSumHashMap.
 *
 * TEMPLATE
 *   seen = {}                                 // value (or key derived from it) -> index / count
 *   for i, x in a:
 *       if need(x) in seen: found a pair (seen[need(x)], i)
 *       seen[x] = i                           // insert AFTER the lookup: no self-pairing
 *
 * APPROACH
 *   1. Walk once. For x, the partner it needs is target - x.
 *   2. If that partner was already seen, return both indices.
 *   3. Otherwise remember x -> its index.
 *
 * KEY INSIGHT
 *   Turn "search for a partner" (O(n) each) into "look up a partner" (O(1)) by storing what
 *   you have already passed. Looking up before inserting means an element can never pair
 *   with itself, yet [3, 3] still works because the first 3 is stored before the second.
 *
 * COMPLEXITY
 *   Time O(n), space O(n). LC 454: O(n^2) time and space instead of O(n^4).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 219  Contains Duplicate II    store last index; pair if i - last <= k
 *   [coded] LC 1010 Pairs Divisible by 60    key = x % 60; partner key = (60 - r) % 60;
 *                                            count pairs instead of returning one
 *   [coded] LC 454  4Sum II                  map of a + b sums; look up -(c + d)
 *           LC 1679 Max K-Sum Pairs          counts; consume one partner per match
 *           LC 532  K-diff Pairs             distinct pairs: count map, check x + k
 *           LC 2342 Max Pair Equal Digit Sum key = digit sum, keep the best value per key
 *           LC 1512 Number of Good Pairs     count[x] pairs added per new x
 *           LC 170  Two Sum III Design       add() stores counts; find() scans the keys
 *
 * PITFALLS
 *   - Insert after the lookup, or x pairs with itself when target == 2x.
 *   - Counting pairs: add the current count of the partner key, THEN increment your own.
 *   - LC 1010: remainder 0 pairs with remainder 0, not with 60.
 *
 * DEEP DIVE
 *   A02_TwoSum, C10_PairsOfSongsDivBy60 (05-Hashing-Prefix-Sum)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class ComplementLookup {

    // Canonical LC 1.
    static int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> seen = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            Integer j = seen.get(target - nums[i]);
            if (j != null) {
                return new int[]{j, i};
            }
            seen.put(nums[i], i);
        }
        return new int[0];
    }

    // LC 219: same value at two indices at most k apart.
    static boolean containsNearbyDuplicate(int[] nums, int k) {
        Map<Integer, Integer> last = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            Integer j = last.put(nums[i], i);     // put returns the previous index
            if (j != null && i - j <= k) {
                return true;
            }
        }
        return false;
    }

    // LC 1010: count pairs whose total duration is divisible by 60.
    static int numPairsDivisibleBy60(int[] time) {
        int[] count = new int[60];
        int pairs = 0;
        for (int t : time) {
            int r = t % 60;
            pairs += count[(60 - r) % 60];
            count[r]++;
        }
        return pairs;
    }

    // LC 454: count (i, j, k, l) with a[i] + b[j] + c[k] + d[l] == 0.
    static int fourSumCount(int[] a, int[] b, int[] c, int[] d) {
        Map<Integer, Integer> sums = new HashMap<>();
        for (int x : a) {
            for (int y : b) {
                sums.merge(x + y, 1, Integer::sum);
            }
        }
        int count = 0;
        for (int x : c) {
            for (int y : d) {
                count += sums.getOrDefault(-(x + y), 0);
            }
        }
        return count;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 1 [2,7,11,15] t=9",
                Arrays.toString(twoSum(new int[]{2, 7, 11, 15}, 9)), "[0, 1]");
        check("LC 1 [3,2,4] t=6", Arrays.toString(twoSum(new int[]{3, 2, 4}, 6)), "[1, 2]");
        check("LC 1 [3,3] t=6", Arrays.toString(twoSum(new int[]{3, 3}, 6)), "[0, 1]");

        check("LC 219 [1,2,3,1] k=3", containsNearbyDuplicate(new int[]{1, 2, 3, 1}, 3), true);
        check("LC 219 [1,0,1,1] k=1", containsNearbyDuplicate(new int[]{1, 0, 1, 1}, 1), true);
        check("LC 219 [1,2,3,1,2,3] k=2",
                containsNearbyDuplicate(new int[]{1, 2, 3, 1, 2, 3}, 2), false);

        check("LC 1010 [30,20,150,100,40]",
                numPairsDivisibleBy60(new int[]{30, 20, 150, 100, 40}), 3);
        check("LC 1010 [60,60,60]", numPairsDivisibleBy60(new int[]{60, 60, 60}), 3);

        check("LC 454 two ways",
                fourSumCount(new int[]{1, 2}, new int[]{-2, -1}, new int[]{-1, 2}, new int[]{0, 2}),
                2);
        check("LC 454 all zeros",
                fourSumCount(new int[]{0}, new int[]{0}, new int[]{0}, new int[]{0}), 1);
    }
}
