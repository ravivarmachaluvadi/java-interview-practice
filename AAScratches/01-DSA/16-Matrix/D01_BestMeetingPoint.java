/*
 * =====================================================================
 *  Best Meeting Point                             LeetCode 296 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   Given an m x n binary grid where grid[i][j] == 1 marks a friend's home,
 *   choose one cell as the meeting point and return the minimum possible sum
 *   of travel distances. Travel is Manhattan distance: |r1-r2| + |c1-c2|.
 *   The meeting cell may be any cell, occupied or not.
 *
 * EXAMPLE
 *   [[1,0,0,0,1],
 *    [0,0,0,0,0], [0,0,1,0,0]]  -> 6    meet at (0,2): 2 + 2 + 2
 *   [[1,0,0,0,1]]  -> 4    one row, two people: any cell between them
 *   [[1]]          -> 0    a single person is already at the meeting point
 *   [[0,0],[0,0]]  -> 0    nobody to meet
 *
 * APPROACH  (median minimises the sum of absolute deviations)
 *   1. Manhattan distance splits: total = sum|ri - R| + sum|ci - C|. The row
 *      choice R and the column choice C are two independent 1-D problems.
 *   2. Collect the row index of every person, and the column index of every
 *      person, as two separate lists.
 *   3. Sort each list and take its median. Scanning row by row already yields
 *      the rows in sorted order, but the columns do not, hence the sort.
 *   4. Sum |row - medianRow| + |col - medianCol| over all people.
 *      Because the two axes are independent, it does not matter that sorting
 *      the column list broke the pairing with the row list.
 *
 * KEY INSIGHT
 *   The median, not the mean or the centroid, minimises the sum of absolute
 *   deviations. Picture standing on a line: moving one step toward the side
 *   that holds more points reduces the total by (that side's count minus the
 *   other side's), so the optimum is where the counts balance. With an even
 *   number of people every point between the two middle values is optimal, so
 *   the upper median is a safe pick. Remember the pair of ideas: Manhattan
 *   separates into independent axes, and each axis is solved by the median.
 *
 *   Fixed: the original threw IndexOutOfBoundsException on a grid with no
 *   people, because it read the median of an empty list. It now returns 0.
 *
 * COMPLEXITY
 *   Time  O(m*n + p log p)  scan the grid, then sort the p collected columns
 *                           (rows arrive sorted, so only columns need it)
 *   Space O(p)              the two index lists, p = number of people
 *
 * INTERVIEW FOLLOW-UPS
 *   - Drop the sort to O(m*n): collect columns by scanning column-major, or
 *     use two pointers on the sorted lists summing (right - left) per pair.
 *   - Why is the median right but the mean wrong? Give a counterexample.
 *   - Euclidean distance instead of Manhattan: the axes no longer separate,
 *     and the optimum is the geometric median with no closed form.
 *   - Weighted homes (k people per cell), or a forbidden-cells variant.
 *
 * RUN
 *   main() runs 4 cases (typical, single row, single person, empty grid)
 *   and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BestMeetingPoint {

    public static int minTotalDistance(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) return 0;

        List<Integer> rows = new ArrayList<>();
        List<Integer> cols = new ArrayList<>();

        // Step 1: record the row and the column of every person.
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    rows.add(i);
                    cols.add(j);
                }
            }
        }

        // Nobody on the grid: no travel at all.
        if (rows.isEmpty()) return 0;

        // Step 2: rows already came out sorted (row-major scan); columns did not.
        Collections.sort(rows);
        Collections.sort(cols);

        // Step 3: the upper median of each axis is an optimal meeting coordinate.
        int medianRow = rows.get(rows.size() / 2);
        int medianCol = cols.get(cols.size() / 2);

        // Step 4: the axes are independent, so the broken row/col pairing is fine.
        int totalDistance = 0;
        for (int i = 0; i < rows.size(); i++) {
            totalDistance += Math.abs(rows.get(i) - medianRow)
                           + Math.abs(cols.get(i) - medianCol);
        }
        return totalDistance;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] spread = {{1, 0, 0, 0, 1},
                          {0, 0, 0, 0, 0},
                          {0, 0, 1, 0, 0}};
        print("case 1 (three people)", minTotalDistance(spread), 6);

        int[][] oneRow = {{1, 0, 0, 0, 1}};
        print("case 2 (even count, one row)", minTotalDistance(oneRow), 4);

        int[][] single = {{1}};
        print("case 3 (single person)", minTotalDistance(single), 0);

        int[][] nobody = {{0, 0}, {0, 0}};
        print("case 4 (nobody on the grid)", minTotalDistance(nobody), 0);
    }
}
