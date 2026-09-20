/*
 * =====================================================================
 *  Triangle (minimum top-to-bottom path sum)        LeetCode 120 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given a triangle stored as a list of rows, where row i has i + 1 numbers,
 *   return the smallest sum of a path from the top cell to any cell of the last row.
 *   From index col on row r you may only step to index col or col + 1 on row r + 1.
 *   Values can be negative, so you cannot greedily take the smaller neighbour.
 *
 * EXAMPLE
 *   [[2],[3,4],[6,5,7],[4,1,8,3]]  ->  11    because 2 + 3 + 5 + 1 = 11
 *   [[-10]]                        ->  -10   single cell, the path is the cell itself
 *   [[-1],[2,3],[1,-1,-3]]         ->  -1    because -1 + 3 + -3 = -1 (negatives matter)
 *
 * APPROACH  (bottom-up DP on one rolling 1D array)
 *   1. dp has n slots, one per column of the widest (last) row.
 *   2. Seed dp with the last row: the best cost of "already standing on the bottom".
 *   3. Walk rows upwards, from n - 2 to 0. For each valid column col on that row:
 *        dp[col] = triangle[row][col] + min(dp[col], dp[col + 1])
 *      dp[col] on the right-hand side still holds the row below (the "straight down"
 *      neighbour) and dp[col + 1] holds the "down-right" neighbour, so both children
 *      are read before dp[col] is overwritten.
 *   4. After processing row 0 the answer sits in dp[0].
 *
 * KEY INSIGHT
 *   Going bottom-up removes the boundary handling entirely: top-down would need to ask
 *   "does the parent cell exist?" at both edges of every row, while bottom-up only ever
 *   reads dp[col] and dp[col + 1], which are always inside the row below. The same
 *   single-array trick works for any grid DP whose transitions only look one row back,
 *   and because a row is written left to right using values from the row below, no
 *   second array or backup copy is needed.
 *
 * COMPLEXITY
 *   Time  O(n^2)  every cell of the triangle is relaxed exactly once (n(n+1)/2 cells)
 *   Space O(n)    one array as wide as the bottom row; the input is left untouched
 *
 * INTERVIEW FOLLOW-UPS
 *   - Reconstruct the actual path, not just its cost (store the chosen child per cell).
 *   - Do it top-down instead and handle the two edge columns that have one parent.
 *   - Same shape on a rectangle: Minimum Falling Path Sum (LeetCode 931).
 *   - What breaks if you reuse one array in the top-down direction? (values overwritten
 *     before their sibling reads them, so you would need to iterate right to left)
 *
 * RUN
 *   main() runs 3 cases (typical, single-row edge, all-negatives) and prints
 *   actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Triangle {

    public static int minimumTotal(List<List<Integer>> triangle) {
        int n = triangle.size();

        // dp[col] = cheapest cost to reach the bottom starting from (currentRow, col).
        // Seed it with the bottom row itself: standing there already costs its value.
        int[] dp = new int[n];
        List<Integer> lastRow = triangle.get(n - 1);
        for (int col = 0; col < n; col++) {
            dp[col] = lastRow.get(col);
        }

        // Fold the triangle upwards, one row at a time.
        for (int row = n - 2; row >= 0; row--) {
            for (int col = 0; col <= row; col++) {
                // dp[col] and dp[col + 1] still describe the row BELOW at this moment,
                // so this reads both children before overwriting the parent slot.
                int bestChild = Math.min(dp[col], dp[col + 1]);
                dp[col] = triangle.get(row).get(col) + bestChild;
            }
        }

        return dp[0];
    }

    /** Builds a triangle from rows given as plain int arrays, for readable test cases. */
    private static List<List<Integer>> triangleOf(int[]... rows) {
        List<List<Integer>> triangle = new ArrayList<>();
        for (int[] row : rows) {
            List<Integer> asList = new ArrayList<>();
            for (int value : row) {
                asList.add(value);
            }
            triangle.add(asList);
        }
        return triangle;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // Case 1 - typical: 2 + 3 + 5 + 1 = 11
        List<List<Integer>> classic =
                triangleOf(new int[]{2}, new int[]{3, 4},
                        new int[]{6, 5, 7}, new int[]{4, 1, 8, 3});
        print("case 1 " + Arrays.toString(classic.toArray()), minimumTotal(classic), 11);

        // Case 2 - edge: a single cell, so the answer is that cell
        List<List<Integer>> single = triangleOf(new int[]{-10});
        print("case 2 " + Arrays.toString(single.toArray()), minimumTotal(single), -10);

        // Case 3 - tricky: negatives mean the locally smaller step is not always right
        // (taking 2 over 3 on row 1 leads to a worse total than taking 3)
        List<List<Integer>> negatives =
                triangleOf(new int[]{-1}, new int[]{2, 3}, new int[]{1, -1, -3});
        print("case 3 " + Arrays.toString(negatives.toArray()), minimumTotal(negatives), -1);
    }
}
