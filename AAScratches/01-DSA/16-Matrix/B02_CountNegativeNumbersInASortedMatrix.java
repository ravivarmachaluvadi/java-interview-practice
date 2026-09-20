/*
 * =====================================================================
 *  Count Negative Numbers in a Sorted Matrix        LeetCode 1351 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an m x n grid sorted in NON-INCREASING order both left-to-right within
 *   each row and top-to-bottom within each column, return how many entries are
 *   negative. Because of that sorting, once a row turns negative it stays negative
 *   to the right, and once a column turns negative it stays negative downward.
 *
 * EXAMPLE
 *   [[4,3,2,-1],[3,2,1,-1],[1,1,-1,-2],[-1,-1,-2,-3]]  ->  8
 *   [[3,2],[1,0]]                                      ->  0   (nothing is negative)
 *   [[-1]]                                             ->  1   (single negative cell)
 *
 * APPROACH  (staircase walk from the bottom-left corner)
 *   1. Start at the bottom-left cell: row = m-1, col = 0. This corner is special
 *      because it is the SMALLEST value in its row and the LARGEST in its column,
 *      so each comparison rules out a whole row or a whole column.
 *   2. If grid[row][col] < 0, every cell to its right in that row is also negative,
 *      so add (n - col) and move up one row.
 *   3. Otherwise the cell is non-negative, so everything above it in this column is
 *      non-negative too; move right one column.
 *   4. Stop when the walk leaves the grid. Every step eliminates one row or one column.
 *
 * KEY INSIGHT
 *   In a doubly sorted matrix, start at a corner where the two sort directions
 *   DISAGREE (bottom-left or top-right). From there a single comparison discards an
 *   entire row or column, so the scan is O(m + n) instead of O(m * n). This exact walk
 *   solves Search a 2D Matrix II and underpins kth-smallest-in-a-sorted-matrix; the
 *   top-left and bottom-right corners are useless because both directions agree there.
 *
 * COMPLEXITY
 *   Time  O(m + n)  each step moves up or right once and never backtracks
 *   Space O(1)      two indices and a counter
 *
 * INTERVIEW FOLLOW-UPS
 *   - Binary search each row for the first negative: O(m log n) - see the second
 *     method below; the staircase beats it whenever n is comparable to m.
 *   - Search a 2D Matrix II (LeetCode 240): same walk, but test for a target value.
 *   - Rows sorted but columns NOT sorted: the staircase breaks; fall back to per-row
 *     binary search.
 *   - Count values greater than k instead of 0: same walk with a different comparison.
 *
 * RUN
 *   main() runs 3 cases (LeetCode sample, all non-negative edge, single negative cell)
 *   against both methods and prints actual vs expected.
 */

class CountNegativeNumbersInASortedMatrix {

    /** O(m + n) staircase walk starting at the bottom-left corner. */
    public static int countNegatives(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int row = m - 1, col = 0;   // bottom-left: smallest in its row, largest in its column
        int count = 0;

        while (row >= 0 && col < n) {
            if (grid[row][col] < 0) {
                // the row is non-increasing, so every cell from col to n-1 is negative too
                count += (n - col);
                row--;
            } else {
                // the column is non-increasing downward, so everything above is non-negative
                col++;
            }
        }
        return count;
    }

    /** O(m log n) alternative: binary search each row for the first negative index. */
    public static int countNegativesBinarySearch(int[][] grid) {
        int n = grid[0].length;
        int count = 0;
        for (int[] row : grid) {
            int lo = 0, hi = n;     // hi = n means "no negative in this row"
            while (lo < hi) {
                int mid = lo + (hi - lo) / 2;
                if (row[mid] < 0) {
                    hi = mid;       // mid is negative, so the first negative is at or before it
                } else {
                    lo = mid + 1;
                }
            }
            count += n - lo;
        }
        return count;
    }

    private static void print(String label, int[][] grid, int expected) {
        System.out.println(label + ": staircase=" + countNegatives(grid)
                + " binarySearch=" + countNegativesBinarySearch(grid)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] sample = {{4, 3, 2, -1}, {3, 2, 1, -1}, {1, 1, -1, -2}, {-1, -1, -2, -3}};
        int[][] noNegatives = {{3, 2}, {1, 0}};
        int[][] singleNegative = {{-1}};

        print("case 1 (LeetCode sample 4x4)", sample, 8);
        print("case 2 (all non-negative, zero is not negative)", noNegatives, 0);
        print("case 3 (1x1 negative edge)", singleNegative, 1);
    }
}
