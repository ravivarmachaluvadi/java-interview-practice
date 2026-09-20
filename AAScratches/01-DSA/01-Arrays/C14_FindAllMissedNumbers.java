/*
 * =====================================================================
 *  Find All Numbers Disappeared in an Array           LeetCode 448 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   nums has length n and every value is in 1..n (values may repeat). Return every
 *   number in 1..n that does not appear, in ascending order. Target: O(n) time and no
 *   extra space beyond the output list, so a boolean[] or HashSet is off the table.
 *
 * EXAMPLE
 *   nums = [4, 3, 2, 7, 8, 2, 3, 1]  ->  [5, 6]
 *   nums = [1, 1]                    ->  [2]      the duplicate leaves one slot unmarked
 *   nums = [1, 2, 3]                 ->  []       nothing missing
 *
 * APPROACH  (sign marking in place)
 *   1. For each element read v = |nums[i]|. The absolute value matters because an
 *      earlier step may already have flipped this entry negative.
 *   2. Record "v was seen" by making nums[v - 1] negative, if it is not negative already.
 *   3. Second pass: every index j still holding a positive value was never marked, so
 *      j + 1 is missing. Indices are visited in order, so the output comes out sorted.
 *
 * KEY INSIGHT
 *   The array is its own bitmap: the sign of slot v - 1 is a free boolean "v seen", and
 *   the magnitude still carries the original value so later reads are never confused.
 *   Compare cyclic sort (C13): swapping moves values to their home index, sign marking
 *   leaves the order intact and touches only one bit. Both need values in 1..n.
 *
 * COMPLEXITY
 *   Time  O(n)  two linear passes
 *   Space O(1)  extra; the input is mutated (restore with Math.abs if the caller cares)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Restore the input afterwards: one more pass applying Math.abs to every slot.
 *   - Values in 0..n-1 instead of 1..n: 0 cannot be negated; add n as the mark and read % n.
 *   - Find the duplicates instead (LeetCode 442): a slot already negative when you mark it.
 *   - Input is read-only: you need O(n) extra space (a copy, boolean[] or set).
 *
 * RUN
 *   main() runs 3 cases (typical, duplicate causing a gap, nothing missing) and prints
 *   actual vs expected.
 */

import java.util.ArrayList;
import java.util.List;

public class C14_FindAllMissedNumbers {

    public static List<Integer> findDisappearedNumbers(int[] nums) {
        int n = nums.length;

        // Pass 1: for each value v, flip the sign of slot v - 1 to record "v seen".
        for (int i = 0; i < n; i++) {
            int seenValue = Math.abs(nums[i]);   // abs: this slot may already be marked
            int markIndex = seenValue - 1;
            if (nums[markIndex] > 0) {
                nums[markIndex] = -nums[markIndex];
            }
        }

        // Pass 2: a slot that is still positive was never marked, so j + 1 never appeared.
        List<Integer> missing = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            if (nums[j] > 0) {
                missing.add(j + 1);
            }
        }
        return missing;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Typical: LeetCode example 1.
        print("case 1 typical      ",
                findDisappearedNumbers(new int[]{4, 3, 2, 7, 8, 2, 3, 1}), "[5, 6]");

        // Edge: a duplicate means exactly one slot is never marked.
        print("case 2 duplicate    ", findDisappearedNumbers(new int[]{1, 1}), "[2]");

        // Edge: complete permutation, nothing missing.
        print("case 3 none missing ", findDisappearedNumbers(new int[]{1, 2, 3}), "[]");
    }
}
