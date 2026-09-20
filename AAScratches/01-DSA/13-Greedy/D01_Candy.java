/*
 * =====================================================================
 *  Candy                                     LeetCode 135 | Hard  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Children stand in a line, child i has rating ratings[i]. Every child gets at
 *   least one candy, and a child with a strictly higher rating than an immediate
 *   neighbour must get strictly more candies than that neighbour. Return the
 *   minimum total candies. Equal neighbours constrain nothing.
 *
 * EXAMPLE
 *   [1, 0, 2]        ->  5   candies 2, 1, 2
 *   [1, 2, 2]        ->  4   candies 1, 2, 1  (the two 2s need not match)
 *   [1, 2, 3, 2, 1]  ->  9   candies 1, 2, 3, 2, 1  - the peak serves both sides
 *   [5]              ->  1   single child
 *   [4, 4, 4]        ->  3   no constraint between equals
 *
 * APPROACH  (two passes, then take the max)
 *   1. Left-to-right: left[0] = 1. If ratings[i] > ratings[i-1] then
 *      left[i] = left[i-1] + 1, else left[i] = 1. This satisfies every
 *      "higher than my LEFT neighbour" rule, minimally.
 *   2. Right-to-left: the mirror image into right[], satisfying every
 *      "higher than my RIGHT neighbour" rule, minimally.
 *   3. Answer = sum of max(left[i], right[i]). Taking the max satisfies both
 *      families of constraints at once, and it is the smallest value that can.
 *
 * KEY INSIGHT
 *   One scan cannot do it: walking left to right you cannot know that the child
 *   ahead of you is about to start a long descent, which would force your count
 *   up retroactively. So split the constraint set by DIRECTION, solve each
 *   direction independently where it is a trivial running counter, then combine
 *   pointwise with max. Max is correct because each child's requirement is
 *   "at least left[i] AND at least right[i]" - the tightest value meeting both
 *   lower bounds is their maximum.
 *   Pattern to recognise: constraints from both sides of a sequence -> prefix
 *   pass + suffix pass + pointwise combine (also Trapping Rain Water, Product of
 *   Array Except Self).
 *
 * COMPLEXITY
 *   Time  O(n)  three linear passes
 *   Space O(n)  two helper arrays; can be cut to O(1) with a peak/valley scan
 *
 * INTERVIEW FOLLOW-UPS
 *   - Do it in O(1) extra space (count up-slopes and down-slopes, fix the peak).
 *   - What changes if EQUAL ratings must get equal candies?
 *   - Extend to a circle instead of a line - why do the two passes break?
 *   - Every child must get at least k candies instead of 1 - what is the answer?
 *
 * RUN
 *   main() runs 5 cases (typical, equal neighbours, peak, single child, all
 *   equal) and prints actual vs expected.
 */

import java.util.Arrays;

class Candy {

    /**
     * Minimum total candies satisfying both neighbour constraints.
     */
    public static int candy(int[] ratings) {
        int n = ratings.length;
        if (n == 0) return 0;

        int[] left = new int[n];    // enough to beat my LEFT neighbour
        int[] right = new int[n];   // enough to beat my RIGHT neighbour
        left[0] = 1;
        right[n - 1] = 1;

        for (int i = 1; i < n; i++) {
            left[i] = ratings[i] > ratings[i - 1] ? left[i - 1] + 1 : 1;
        }
        for (int i = n - 2; i >= 0; i--) {
            right[i] = ratings[i] > ratings[i + 1] ? right[i + 1] + 1 : 1;
        }

        int total = 0;
        for (int i = 0; i < n; i++) {
            // Both bounds must hold, so take the tighter (larger) one.
            total += Math.max(left[i], right[i]);
        }
        return total;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] c1 = {1, 0, 2};
        print("case 1 " + Arrays.toString(c1), candy(c1), 5);

        int[] c2 = {1, 2, 2};
        print("case 2 " + Arrays.toString(c2), candy(c2), 4);

        // tricky: a peak whose left run and right run must both be respected
        int[] c3 = {1, 2, 3, 2, 1};
        print("case 3 " + Arrays.toString(c3), candy(c3), 9);

        // edge: one child
        int[] c4 = {5};
        print("case 4 " + Arrays.toString(c4), candy(c4), 1);

        // edge: all equal, so no constraint fires at all
        int[] c5 = {4, 4, 4};
        print("case 5 " + Arrays.toString(c5), candy(c5), 3);
    }
}
