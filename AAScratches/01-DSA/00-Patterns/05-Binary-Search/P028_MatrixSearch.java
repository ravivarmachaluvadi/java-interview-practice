/*
 * =====================================================================
 *  P028 Binary Search in a 2D Matrix   Canonical LC 74 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 74, Search a 2D Matrix)
 *   Each row is sorted, and the first value of each row is greater than the last value of
 *   the previous row. Return whether target is in the matrix, in O(log(m * n)).
 *
 * EXAMPLE
 *   [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target 3   ->  true
 *   [[1,3,5,7],[10,11,16,20],[23,30,34,60]], target 13  ->  false
 *
 * RECOGNIZE WHEN
 *   - A matrix whose rows (and maybe columns) are sorted, and you must search or count.
 *   - "kth smallest in a sorted matrix", "count values <= x in the matrix".
 *   Not this if: you must visit connected cells -> P065_GridFloodFill; there is no
 *   ordering at all -> a plain scan.
 *
 * TEMPLATE
 *   fully sorted (LC 74):  treat it as one array of m * n; index k is cell (k / n, k % n)
 *   rows AND columns sorted (LC 240): start top-right; bigger than target -> go left,
 *                                     smaller -> go down               (O(m + n))
 *   kth smallest (LC 378): binary search the VALUE; count cells <= mid with the staircase
 *
 * APPROACH
 *   1. lo = 0, hi = m * n - 1 over a virtual flattened array.
 *   2. Map mid back to (mid / n, mid % n) and do a normal binary search.
 *
 * KEY INSIGHT
 *   The "row starts after the previous row ends" rule makes the matrix one sorted list in
 *   disguise. When only rows and columns are sorted, the top-right corner is the useful
 *   start: every step there removes a whole row or a whole column.
 *
 * COMPLEXITY
 *   LC 74: O(log(m * n)). LC 240: O(m + n). LC 378: O(n log(max - min)). Space O(1).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 240  Search 2D Matrix II      staircase from the top-right corner
 *   [coded] LC 378  Kth Smallest in Matrix   binary search on value + staircase count;
 *                                            the first value with count >= k
 *           LC 1351 Count Negatives          staircase counting from the bottom-left
 *           LC 668  Kth Number in Mult Table count(x) = sum of min(x / i, n)
 *           LC 378 with a heap               -> P044_KWayMerge
 *
 * PITFALLS
 *   - LC 74 index mapping uses the column count n for both / and %.
 *   - LC 378: the answer must be a value IN the matrix; "first value with count >= k"
 *     guarantees it, "count == k" does not.
 *   - Empty matrix or empty rows: guard before reading matrix[0].length.
 *
 * DEEP DIVE
 *   C01_Search2DMatrix (03-Binary-Search),
 *   B02_CountNegativeNumbersInASortedMatrix (16-Matrix)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
class MatrixSearch {

    // Canonical LC 74.
    static boolean searchMatrix(int[][] m, int target) {
        int rows = m.length;
        int cols = m[0].length;
        int lo = 0;
        int hi = rows * cols - 1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            int v = m[mid / cols][mid % cols];
            if (v == target) {
                return true;
            } else if (v < target) {
                lo = mid + 1;
            } else {
                hi = mid - 1;
            }
        }
        return false;
    }

    // LC 240: rows and columns sorted, nothing more.
    static boolean searchMatrixII(int[][] m, int target) {
        int r = 0;
        int c = m[0].length - 1;
        while (r < m.length && c >= 0) {
            if (m[r][c] == target) {
                return true;
            } else if (m[r][c] > target) {
                c--;                             // everything below in this column is bigger
            } else {
                r++;                             // everything left in this row is smaller
            }
        }
        return false;
    }

    // LC 378: n x n, rows and columns sorted.
    static int kthSmallest(int[][] m, int k) {
        int n = m.length;
        int lo = m[0][0];
        int hi = m[n - 1][n - 1];
        while (lo < hi) {
            int mid = lo + (hi - lo) / 2;
            if (countAtMost(m, mid) >= k) {
                hi = mid;
            } else {
                lo = mid + 1;
            }
        }
        return lo;
    }

    // Staircase from the bottom-left: cells <= x in O(n).
    private static int countAtMost(int[][] m, int x) {
        int n = m.length;
        int r = n - 1;
        int c = 0;
        int count = 0;
        while (r >= 0 && c < n) {
            if (m[r][c] <= x) {
                count += r + 1;                  // the whole column above is <= x too
                c++;
            } else {
                r--;
            }
        }
        return count;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[][] a = {{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}};
        check("LC 74 target 3", searchMatrix(a, 3), true);
        check("LC 74 target 13", searchMatrix(a, 13), false);
        check("LC 74 target 60 last cell", searchMatrix(a, 60), true);

        int[][] b = {{1, 4, 7, 11, 15}, {2, 5, 8, 12, 19}, {3, 6, 9, 16, 22},
                {10, 13, 14, 17, 24}, {18, 21, 23, 26, 30}};
        check("LC 240 target 5", searchMatrixII(b, 5), true);
        check("LC 240 target 20", searchMatrixII(b, 20), false);

        check("LC 378 k=8", kthSmallest(new int[][]{{1, 5, 9}, {10, 11, 13}, {12, 13, 15}}, 8), 13);
        check("LC 378 1x1", kthSmallest(new int[][]{{-5}}, 1), -5);
        check("LC 378 k=3 duplicates", kthSmallest(new int[][]{{1, 2}, {1, 3}}, 3), 2);
    }
}
