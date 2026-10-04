/*
 * =====================================================================
 *  P022 HashSet: Existence and Run Detection   Canonical LC 128 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 128, Longest Consecutive Sequence)
 *   nums is unsorted. Return the length of the longest run of consecutive integers that
 *   all appear in nums (order in the array does not matter). Must run in O(n).
 *
 * EXAMPLE
 *   [100, 4, 200, 1, 3, 2]          ->  4     1, 2, 3, 4
 *   [0, 3, 7, 2, 5, 8, 4, 6, 0, 1]  ->  9     0..8
 *   []                              ->  0
 *
 * RECOGNIZE WHEN
 *   - O(1) "have I seen x?" checks: duplicates, membership, set difference.
 *   - "consecutive values" (not consecutive positions) with an O(n) requirement, so
 *     sorting (O(n log n)) is ruled out.
 *   - Chains where each element points to the next value (x -> x + 1, x -> x * x).
 *   Not this if: you need counts, not presence -> P021_FrequencyCanonicalKey; consecutive
 *   POSITIONS in the array -> a sliding window (P017_VariableWindowLongest).
 *
 * TEMPLATE
 *   set = all values
 *   for x in set:
 *       if prev(x) not in set:              // x starts a chain; only starts walk forward
 *           length = 1
 *           while next(x) in set: x = next(x); length++
 *           best = max(best, length)
 *
 * APPROACH
 *   1. Put every value in a HashSet (duplicates vanish).
 *   2. Only a value whose predecessor is missing starts a run; walk forward from it.
 *
 * KEY INSIGHT
 *   Starting the walk only at run starts means each value is visited at most twice (once
 *   in the outer loop, once inside one run), so the nested loop is still O(n) total.
 *   Without the "predecessor missing" check it degrades to O(n^2).
 *
 * COMPLEXITY
 *   Time O(n) expected, space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 217  Contains Duplicate       set.add returns false on a repeat
 *   [coded] LC 2501 Longest Square Streak    next(x) = x * x; start where sqrt(x) is absent
 *           LC 1365 Smaller Than Current     counts per value + prefix over the value range
 *           LC 349  Intersection of Arrays   set of one, filter the other
 *           LC 2215 Difference of Two Arrays two sets, removeAll each way
 *           LC 41   First Missing Positive   O(1) space -> P005_CyclicSortIndexAsHash
 *
 * PITFALLS
 *   - Iterate over the SET, not the array: duplicates in the array would repeat walks.
 *   - LC 2501: x * x overflows int for large x; use long.
 *   - LC 2501: a streak needs at least 2 numbers; return -1 otherwise.
 *
 * DEEP DIVE
 *   D01_LongestConsecutiveSequence, A01_ContainsDuplicate (05-Hashing-Prefix-Sum)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.HashSet;
import java.util.Set;

class HashSetRunDetection {

    // Canonical LC 128.
    static int longestConsecutive(int[] nums) {
        Set<Integer> set = new HashSet<>();
        for (int x : nums) {
            set.add(x);
        }
        int best = 0;
        for (int x : set) {
            if (!set.contains(x - 1)) {
                int length = 1;
                while (set.contains(x + length)) {
                    length++;
                }
                best = Math.max(best, length);
            }
        }
        return best;
    }

    // LC 217.
    static boolean containsDuplicate(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        for (int x : nums) {
            if (!seen.add(x)) {
                return true;
            }
        }
        return false;
    }

    // LC 2501: longest chain x, x^2, x^4, ... (any order in the array); -1 if under 2.
    static int longestSquareStreak(int[] nums) {
        Set<Long> set = new HashSet<>();
        for (int x : nums) {
            set.add((long) x);
        }
        int best = 0;
        for (long x : set) {
            long root = (long) Math.sqrt(x);
            if (root * root == x && set.contains(root)) {
                continue;                          // not a chain start
            }
            int length = 1;
            long cur = x;
            while (cur <= 100_000 && set.contains(cur * cur)) {
                cur *= cur;
                length++;
            }
            best = Math.max(best, length);
        }
        return best >= 2 ? best : -1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 128 [100,4,200,1,3,2]", longestConsecutive(new int[]{100, 4, 200, 1, 3, 2}), 4);
        check("LC 128 [0,3,7,2,5,8,4,6,0,1]",
                longestConsecutive(new int[]{0, 3, 7, 2, 5, 8, 4, 6, 0, 1}), 9);
        check("LC 128 empty", longestConsecutive(new int[]{}), 0);
        check("LC 128 duplicates [1,2,0,1]", longestConsecutive(new int[]{1, 2, 0, 1}), 3);

        check("LC 217 [1,2,3,1]", containsDuplicate(new int[]{1, 2, 3, 1}), true);
        check("LC 217 [1,2,3,4]", containsDuplicate(new int[]{1, 2, 3, 4}), false);

        check("LC 2501 [4,3,6,16,8,2]", longestSquareStreak(new int[]{4, 3, 6, 16, 8, 2}), 3);
        check("LC 2501 [2,3,5,6,7]", longestSquareStreak(new int[]{2, 3, 5, 6, 7}), -1);
        check("LC 2501 [2,4,16,256,65536]",
                longestSquareStreak(new int[]{2, 4, 16, 256, 65536}), 5);
    }
}
