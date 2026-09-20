/*
 * =====================================================================
 *  Matrix Chain Multiplication                    GFG | Hard    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   You are given an array arr of length N describing a chain of N-1 matrices:
 *   matrix i has dimensions arr[i-1] x arr[i]. Matrix multiplication is associative,
 *   so the chain can be bracketed in many ways. Return the minimum number of scalar
 *   multiplications needed to multiply the whole chain.
 *
 * EXAMPLE
 *   arr = [10, 20, 30, 40, 50]  ->  38000   matrices 10x20, 20x30, 30x40, 40x50
 *   arr = [10, 20, 30]          ->  6000    only one bracketing: (10x20)(20x30)
 *   arr = [10, 20]              ->  0       a single matrix, nothing to multiply
 *   arr = [40, 20, 30, 10, 30]  ->  26000   the classic textbook chain
 *
 * APPROACH  (interval DP: try every split point k)
 *   1. State is the interval (i, j): the cost of collapsing matrices i..j into one.
 *      Because matrix i is arr[i-1] x arr[i], the chain starts at i = 1, not 0.
 *   2. Base case i == j: a single matrix is already collapsed, cost 0.
 *   3. For every k in [i, j-1], split into (i..k) and (k+1..j). Those two blocks
 *      collapse to arr[i-1] x arr[k] and arr[k] x arr[j], so merging them costs
 *      arr[i-1] * arr[k] * arr[j].
 *   4. Answer for (i, j) is the minimum over k of left + merge + right.
 *   5. Overlapping intervals repeat constantly, so memoise on (i, j). Both the plain
 *      recursion and the memoised version are below; main() runs both.
 *
 * KEY INSIGHT
 *   Do not try to decide the FIRST multiplication. Decide the LAST one: pick the split
 *   k where the two halves finally meet, price that single merge in O(1), and recurse
 *   on both sides. "Loop k across (i, j), price the merge, recurse left and right" is
 *   the template for every interval DP - burst balloons, minimum cost to cut a stick,
 *   palindrome partitioning II. Recognise it whenever the cost of a piece depends on
 *   its neighbours rather than on a single index.
 *
 * COMPLEXITY
 *   Plain recursion: Time O(4^N / N^1.5) - Catalan-many bracketings, and every
 *                    split re-explores both halves from scratch.
 *   Memoised:  Time  O(N^3)  O(N^2) intervals, each scanning O(N) split points.
 *              Space O(N^2)  the memo table, plus O(N) recursion depth.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Convert the memoisation to bottom-up: loop i from N-1 down, j from i+1 up.
 *   - Reconstruct the optimal bracketing, not just its cost (store the best k).
 *   - Burst Balloons (LC 312): same k-split, but k is the LAST balloon burst.
 *   - Why can a greedy "always multiply the cheapest adjacent pair" answer be wrong?
 *
 * RUN
 *   main() runs 4 cases (typical, single matrix, two matrices, textbook chain) through
 *   both the plain and the memoised solver and prints actual vs expected.
 */

import java.util.Arrays;

class MCM {

    /* ---------- 1. Plain recursion (the author's version) ---------- */

    /** Minimum cost to collapse matrices i..j, where matrix m is arr[m-1] x arr[m]. */
    private int func(int[] arr, int i, int j) {
        if (i == j) return 0; // a single matrix needs no multiplication

        int mini = Integer.MAX_VALUE;

        for (int k = i; k < j; k++) {
            // left block is arr[i-1] x arr[k], right block is arr[k] x arr[j]
            int currMultiplication = arr[i - 1] * arr[k] * arr[j];
            int ans = func(arr, i, k) + currMultiplication + func(arr, k + 1, j);
            mini = Math.min(mini, ans);
        }
        return mini;
    }

    public int matrixMultiplication(int[] nums) {
        int N = nums.length;
        if (N < 3) return 0; // fewer than two matrices: nothing to multiply

        int i = 1;     // first matrix of the chain
        int j = N - 1; // last matrix of the chain

        return func(nums, i, j);
    }

    /* ---------- 2. Same recursion, memoised on the interval (i, j) ---------- */

    public int matrixMultiplicationMemo(int[] nums) {
        int N = nums.length;
        if (N < 3) return 0;

        int[][] memo = new int[N][N];
        for (int[] row : memo) Arrays.fill(row, -1); // -1 marks "not computed yet"

        return funcMemo(nums, 1, N - 1, memo);
    }

    private int funcMemo(int[] arr, int i, int j, int[][] memo) {
        if (i == j) return 0;
        if (memo[i][j] != -1) return memo[i][j];

        int mini = Integer.MAX_VALUE;
        for (int k = i; k < j; k++) {
            int cost = funcMemo(arr, i, k, memo)
                     + arr[i - 1] * arr[k] * arr[j]
                     + funcMemo(arr, k + 1, j, memo);
            mini = Math.min(mini, cost);
        }
        return memo[i][j] = mini;
    }

    /* ---------- 3. Driver ---------- */

    private static void check(MCM sol, int[] arr, int expected) {
        System.out.println("arr = " + Arrays.toString(arr)
                + "   recursion = " + sol.matrixMultiplication(arr)
                + "   memo = " + sol.matrixMultiplicationMemo(arr)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        MCM sol = new MCM();

        check(sol, new int[]{10, 20, 30, 40, 50}, 38000); // typical 4-matrix chain
        check(sol, new int[]{10, 20}, 0);                 // edge: one matrix
        check(sol, new int[]{10, 20, 30}, 6000);          // edge: only one bracketing
        check(sol, new int[]{40, 20, 30, 10, 30}, 26000); // tricky: split is not in the middle
    }
}
