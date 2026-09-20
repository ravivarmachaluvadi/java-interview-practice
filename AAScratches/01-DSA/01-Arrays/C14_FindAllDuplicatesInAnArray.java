/*
 * =====================================================================
 *  Find All Duplicates in an Array                    LeetCode 442 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   nums has length n and every value is in 1..n. Each value appears once or twice.
 *   Return every value that appears twice. Target: O(n) time and O(1) extra space
 *   (the output list does not count), which rules out a HashSet or a boolean[].
 *
 * EXAMPLE
 *   nums = [4, 3, 2, 7, 8, 2, 3, 1]  ->  [3, 2]   2 and 3 appear twice (any order accepted)
 *   nums = [1, 1, 2]                 ->  [1]      duplicate sits next to its twin
 *   nums = [1]                       ->  []       nothing repeats
 *
 * APPROACH  (cyclic sort placement)
 *   1. Value v "belongs" at index v - 1, so a clean array reads [1, 2, 3, ...].
 *   2. Walk i from 0. Let home = nums[i] - 1.
 *      - If nums[home] == nums[i], the value is settled: either it is already in its
 *        home slot, or a twin already sits there. Advance i.
 *      - Otherwise swap nums[i] into its home and re-examine index i (do NOT advance).
 *   3. After the pass, scan once more: any index j whose value is not j + 1 holds a
 *      second copy of some value, because that value's own slot is already taken.
 *
 * KEY INSIGHT
 *   The guard is "nums[home] == nums[i]", not "home == i". That one comparison covers
 *   both "already placed" and "a duplicate already occupies the slot"; with the weaker
 *   index check, two equal values would swap with each other forever. Termination: every
 *   swap parks one value permanently, so there are at most n swaps even though i stands
 *   still on a swap. Pattern: values in 1..n (or 0..n-1) are the array's own indices,
 *   so the array can be sorted by "send each value home" with no extra memory.
 *
 * COMPLEXITY
 *   Time  O(n)  at most n swaps plus two linear scans
 *   Space O(1)  extra, beyond the output list; the input is reordered in place
 *
 * INTERVIEW FOLLOW-UPS
 *   - Same problem without swapping: negate nums[|v| - 1] as a "seen" mark (see C14).
 *   - Input must not be modified: HashSet costs O(n) space; Floyd's cycle finds ONE dup (C17).
 *   - Find the missing numbers instead (C14), or the one-missing-one-duplicate pair (C15).
 *   - A value appearing three times: the final scan reports it twice (once per extra copy).
 *
 * RUN
 *   main() runs 3 cases (typical, adjacent duplicate, single element) and prints
 *   actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

class FindAllDuplicatesInAnArray {

    public static List<Integer> findDuplicates(int[] nums) {
        // Phase 1: send every value v to index v - 1.
        int i = 0;
        while (i < nums.length) {
            int home = nums[i] - 1;
            if (nums[home] == nums[i]) {
                i++;   // already home, or a twin already holds the home slot
            } else {
                swap(nums, home, i);   // i is NOT advanced: the value swapped in must be checked
            }
        }

        // Phase 2: a slot whose value is not j + 1 holds the extra copy of a duplicate.
        List<Integer> duplicates = new ArrayList<>();
        for (int j = 0; j < nums.length; j++) {
            if (nums[j] != j + 1) {
                duplicates.add(nums[j]);
            }
        }
        return duplicates;
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Typical: LeetCode example 1.
        print("case 1 typical  ", findDuplicates(new int[]{4, 3, 2, 7, 8, 2, 3, 1}), "[3, 2]");

        // Tricky: duplicate adjacent to its twin, exercising the equality guard.
        print("case 2 adjacent ", findDuplicates(new int[]{1, 1, 2}), "[1]");

        // Edge: single element, nothing repeats.
        print("case 3 single   ", findDuplicates(new int[]{1}), "[]");
    }
}
