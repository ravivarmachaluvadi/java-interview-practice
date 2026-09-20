/*
 * =====================================================================
 *  Leaders in an Array                                  GeeksforGeeks | Easy
 * =====================================================================
 *
 * PROBLEM
 *   An element is a leader if it is strictly greater than EVERY element to its
 *   right. The last element is always a leader (nothing is to its right).
 *   Return all leaders in their original left-to-right order.
 *
 * EXAMPLE
 *   nums = [16, 17, 4, 3, 5, 2]  ->  [17, 5, 2]
 *   nums = [1, 2, 3, 4, 5]       ->  [5]                only the last survives
 *   nums = [5, 4, 3, 2, 1]       ->  [5, 4, 3, 2, 1]    every element is a leader
 *   nums = [3, 3, 3]             ->  [3]                strictly greater: ties lose
 *   nums = []                    ->  []
 *
 * APPROACH  (right-to-left running max)
 *   1. Start at the last element: it is a leader; set maxFromRight to it.
 *   2. Walk i from n-2 down to 0. If nums[i] > maxFromRight, nums[i] beats
 *      everything to its right, so record it and raise maxFromRight to nums[i].
 *   3. Leaders were collected right to left, so reverse the list before returning.
 *
 * KEY INSIGHT
 *   "Greater than everything to my right" only needs ONE number: the max of the
 *   suffix. Scanning from the right lets you carry that suffix max as a single
 *   variable instead of recomputing it per index (O(n^2)). Pattern to recognise:
 *   any per-index question about "all elements to the right" is a right-to-left
 *   scan with a running suffix aggregate (mirror of prefix scans).
 *
 * COMPLEXITY
 *   Time  O(n)  one scan plus an O(k) reverse
 *   Space O(k)  the output list, k = number of leaders
 *
 * INTERVIEW FOLLOW-UPS
 *   - Non-strict leaders (>= everything to the right): change > to >=; then
 *     [3, 3, 3] returns [3, 3, 3].
 *   - Avoid the final reverse: prepend to a LinkedList or fill an array backwards
 *     from a count obtained in the same pass.
 *   - Related: "replace each element with the greatest element to its right"
 *     (LeetCode 1299) is the same scan, just writing the suffix max back in.
 *
 * RUN
 *   main() runs 5 cases (typical, ascending, descending, all equal, empty) and
 *   prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class LeadersInAnArray {

    public static List<Integer> leaders(int[] nums) {
        List<Integer> leaders = new ArrayList<>();
        int n = nums.length;
        if (n == 0) {
            return leaders;
        }

        // The last element has nothing to its right, so it is always a leader.
        int maxFromRight = nums[n - 1];
        leaders.add(maxFromRight);

        for (int i = n - 2; i >= 0; i--) {
            if (nums[i] > maxFromRight) {      // strictly greater than the whole suffix
                leaders.add(nums[i]);
                maxFromRight = nums[i];
            }
        }

        Collections.reverse(leaders);          // collected right to left; restore order
        return leaders;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical   ", leaders(new int[]{16, 17, 4, 3, 5, 2}), "[17, 5, 2]");
        print("case 2 ascending ", leaders(new int[]{1, 2, 3, 4, 5}), "[5]");
        print("case 3 descending", leaders(new int[]{5, 4, 3, 2, 1}), "[5, 4, 3, 2, 1]");
        print("case 4 all equal ", leaders(new int[]{3, 3, 3}), "[3]");
        print("case 5 empty     ", leaders(new int[]{}), "[]");
    }
}
