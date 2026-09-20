/*
 * =====================================================================
 *  Check If An Array Is Consecutive                     GeeksforGeeks | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array, decide whether its values can be rearranged into an
 *   unbroken run where each value is exactly one more than the previous
 *   (i.e. the array is a permutation of some range [min .. min + n - 1]).
 *   Order does not matter. Values may be negative. An empty array returns false.
 *
 * EXAMPLE
 *   nums = [3, 2, 1, 4, 5]    ->  true    permutation of 1..5
 *   nums = [1, 2, 4, 5]       ->  false   3 is missing (span 5, length 4)
 *   nums = [1, 2, 2, 3]       ->  false   2 is duplicated (span 3, length 4)
 *   nums = [7]                ->  true    a single value is a run of length 1
 *   nums = [-2, 0, -1, 1]     ->  true    negatives: -2..1
 *   nums = []                 ->  false
 *
 * APPROACH  (min/max span plus duplicate set)
 *   1. One pass: track min and max, and insert each value into a HashSet.
 *      If set.add() returns false the value is a duplicate: return false at once.
 *   2. After the pass, the values are distinct. They fill a run exactly when the
 *      span max - min + 1 equals the element count n. Return that comparison.
 *   Compute the span in long: with extreme int values max - min overflows int.
 *
 * KEY INSIGHT
 *   n distinct integers whose span is exactly n MUST be every integer in
 *   [min, max]; nothing else fits (pigeonhole). So "consecutive" reduces to two
 *   cheap facts: no duplicates, and span == n. This value-to-index relationship
 *   (value v belongs at slot v - min) is the mental model behind cyclic sort.
 *
 * COMPLEXITY
 *   Time  O(n)       one pass with O(1) set operations
 *   Space O(n)       the set of seen values
 *   (sorting variant: O(n log n) time, O(1) extra space)
 *
 * INTERVIEW FOLLOW-UPS
 *   - O(1) extra space? Sort, then check every adjacent pair differs by exactly 1
 *     (isConsecutiveBySorting below). Trades time for space.
 *   - O(n) time AND O(1) space without sorting? Use the array itself: mark visited
 *     slots at index v - min by negating (needs positive values or a copy).
 *   - Why is the set needed at all? [1, 1, 3] has span 3 and length 3 but is not
 *     consecutive; only the duplicate check catches it.
 *
 * RUN
 *   main() runs 6 cases (typical, gap, duplicate, single, negatives, empty)
 *   through both methods and prints actual vs expected.
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

class CheckIfAnArrayIsConsecutive {

    /** Author's approach: O(n) time, O(n) space. */
    public static boolean isConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) {
            return false;                       // decided explicitly, not by accident
        }

        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        Set<Integer> seen = new HashSet<>();
        for (int num : nums) {
            if (!seen.add(num)) {
                return false;                   // add() returns false on a duplicate
            }
            min = Math.min(min, num);
            max = Math.max(max, num);
        }

        // n distinct values with span n must be exactly the run [min .. max]
        return (long) max - min + 1 == nums.length;   // long guards int overflow
    }

    /** Follow-up variant: O(n log n) time, O(1) extra space (mutates a copy). */
    public static boolean isConsecutiveBySorting(int[] nums) {
        if (nums == null || nums.length == 0) {
            return false;
        }
        int[] sorted = nums.clone();            // keep the caller's array intact
        Arrays.sort(sorted);
        for (int i = 1; i < sorted.length; i++) {
            if (sorted[i] - sorted[i - 1] != 1) {
                return false;                   // a gap (> 1) or a duplicate (== 0)
            }
        }
        return true;
    }

    private static void print(String label, int[] nums, boolean expected) {
        System.out.println(label + ": set=" + isConsecutive(nums)
                + " sort=" + isConsecutiveBySorting(nums) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical  ", new int[]{3, 2, 1, 4, 5}, true);
        print("case 2 gap      ", new int[]{1, 2, 4, 5}, false);
        print("case 3 duplicate", new int[]{1, 2, 2, 3}, false);
        print("case 4 single   ", new int[]{7}, true);
        print("case 5 negatives", new int[]{-2, 0, -1, 1}, true);
        print("case 6 empty    ", new int[]{}, false);
    }
}
