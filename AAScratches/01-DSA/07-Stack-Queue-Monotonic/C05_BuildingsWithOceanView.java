/*
 * =====================================================================
 *  Buildings With an Ocean View                     LeetCode 1762 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   heights[i] is the height of building i in a line; the ocean is to the RIGHT.
 *   Building i has an ocean view if every building to its right is strictly
 *   shorter. Return the indices of all such buildings in increasing order.
 *
 * EXAMPLE
 *   [4, 2, 3, 1]  ->  [0, 2, 3]      1 blocks nothing; 3 blocks 2; 4 beats all
 *   [4, 3, 2, 1]  ->  [0, 1, 2, 3]   strictly decreasing, everyone sees the ocean
 *   [1, 3, 2, 4]  ->  [3]            4 blocks everything to its left
 *   [2, 2, 2]     ->  [2]            equal height blocks the view (strictly shorter)
 *   [7]           ->  [0]            the last building always sees the ocean
 *
 * APPROACH  (right-to-left running maximum)
 *   1. Walk from the last building to the first, keeping maxToRight = tallest
 *      building seen so far (i.e. everything to the right of the current one).
 *   2. If heights[i] > maxToRight the building sees the ocean: record i, update max.
 *   3. Indices were collected right to left, so reverse the list before returning.
 *
 * KEY INSIGHT
 *   "Can see past everything on one side" only depends on the maximum on that side,
 *   so scan from that side and carry one number. This is the degenerate case of a
 *   monotonic stack: a strictly decreasing stack whose only useful element is its
 *   top collapses to a single variable. If the scan must go LEFT to right instead
 *   (streaming input), you need the real stack: pop every building that is <= the
 *   new one, and what remains at the end is the answer.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass plus a reverse of the result
 *   Space O(1)  extra beyond the output list
 *
 * INTERVIEW FOLLOW-UPS
 *   - Ocean on the left: scan left to right with the same running max, no reverse
 *   - Buildings arrive one at a time (left to right): monotonic decreasing stack
 *   - Count how many buildings each one can see (LC 1944): stack with pop counting
 *   - Return heights instead of indices, or use >= if equal heights do not block
 *
 * RUN
 *   main() runs 5 cases (typical, all visible, only last, all equal, single) and
 *   prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class BuildingsWithOceanView {

    public static List<Integer> findBuildings(int[] heights) {
        List<Integer> result = new ArrayList<>();
        int maxToRight = Integer.MIN_VALUE; // nothing to the right of the last building

        for (int i = heights.length - 1; i >= 0; i--) {
            if (heights[i] > maxToRight) { // strictly taller than everything to its right
                result.add(i);
                maxToRight = heights[i];
            }
        }
        Collections.reverse(result); // collected right to left; answer wants increasing
        return result;
    }

    private static void print(String label, int[] heights, String expected) {
        System.out.println(label + " " + Arrays.toString(heights) + " -> "
                + findBuildings(heights) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1", new int[]{4, 2, 3, 1}, "[0, 2, 3]");
        print("case 2", new int[]{4, 3, 2, 1}, "[0, 1, 2, 3]");
        print("case 3", new int[]{1, 3, 2, 4}, "[3]");
        print("case 4", new int[]{2, 2, 2}, "[2]");
        print("case 5", new int[]{7}, "[0]");
    }
}
