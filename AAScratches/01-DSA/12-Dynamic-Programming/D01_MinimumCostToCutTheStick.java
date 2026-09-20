/*
 * =====================================================================
 *  Minimum Cost to Cut a Stick                    LeetCode 1547 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   A wooden stick of length n is labelled 0..n. You are given cuts[], the positions
 *   where cuts must be made. A cut costs the length of the piece it is made in, and
 *   that piece then splits in two. You may perform the cuts in any order. Return the
 *   minimum total cost of performing all the cuts.
 *
 * EXAMPLE
 *   n = 7, cuts = [1, 3, 4, 5]     ->  16   (cutting left-to-right costs 7+6+4+3 = 20)
 *   n = 9, cuts = [5, 6, 1, 4, 2]  ->  22   the LeetCode sample
 *   n = 5, cuts = [2]              ->  5    edge: one cut, only one possible cost
 *
 * APPROACH  (interval DP - MCM with sentinel padding)
 *   1. Sort cuts and pad it with 0 at the front and n at the back:
 *      padded = [0, c1, c2, ..., ck, n]. Now every piece of stick is exactly the gap
 *      between two padded entries, so a piece can be named by an index range.
 *   2. State (i, j) = minimum cost to make all the cuts padded[i..j], given that the
 *      piece currently being worked on runs from padded[i-1] to padded[j+1].
 *      The sentinels are what make padded[i-1] and padded[j+1] always exist.
 *   3. Base case i > j: no cuts left in this piece, cost 0.
 *   4. For every k in [i, j], make cut k FIRST. That cut costs the whole current piece,
 *      padded[j+1] - padded[i-1], and then splits it into (i..k-1) and (k+1..j).
 *   5. Answer is the minimum over k, starting from func(1, cuts.size()).
 *   6. Intervals repeat heavily, so memoise on (i, j). Plain and memoised versions are
 *      both below and main() runs both.
 *
 * KEY INSIGHT
 *   Two ideas stacked. First: sorting is not cosmetic - once cuts are sorted, the cuts
 *   inside a piece form a contiguous index range, which is what makes an interval state
 *   possible at all. Second: this is MCM with the merge cost replaced by the cut cost.
 *   Choose which cut happens FIRST in the piece (the same way MCM chooses which
 *   multiplication happens LAST), price that one cut in O(1), recurse on both sides.
 *
 * COMPLEXITY
 *   Plain recursion: Time O(k!)-ish - every ordering of the cuts is re-explored.
 *   Memoised:  Time  O(k^3)  O(k^2) intervals, each scanning O(k) first-cut choices.
 *              Space O(k^2)  the memo table, plus O(k) recursion depth. k = cuts.size().
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why is greedy ("always cut in the middle", or "always cheapest cut first") wrong?
 *   - Convert to bottom-up: i from k down to 1, j from i up to k.
 *   - Return the actual order of cuts, not just the cost.
 *   - Burst Balloons (LC 312) and MCM: name what changes in the transition, not the shape.
 *
 * RUN
 *   main() runs 3 cases (typical, LeetCode sample, single-cut edge) through both the
 *   plain and the memoised solver and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MinimumCostToCutTheStick {

    /* ---------- 1. Plain recursion (the author's version) ---------- */

    /**
     * Minimum cost to perform cuts[i..j], where the piece being cut spans
     * cuts[i-1] .. cuts[j+1] in the padded array.
     */
    private int func(int i, int j, int[] cuts) {
        if (i > j) return 0; // no cuts left inside this piece

        int mini = Integer.MAX_VALUE;
        for (int k = i; k <= j; k++) {
            // make cut k first: it costs the full length of the current piece
            int ans = cuts[j + 1] - cuts[i - 1] + func(i, k - 1, cuts) + func(k + 1, j, cuts);
            mini = Math.min(mini, ans);
        }
        return mini;
    }

    public int minCost(int n, List<Integer> cuts) {
        int[] padded = pad(n, cuts);
        return func(1, cuts.size(), padded);
    }

    /* ---------- 2. Same recursion, memoised on the interval (i, j) ---------- */

    public int minCostMemo(int n, List<Integer> cuts) {
        int c = cuts.size();
        int[] padded = pad(n, cuts);

        // i ranges over 1..c+1 and j over 0..c, so c+2 is a safe bound for both
        int[][] memo = new int[c + 2][c + 2];
        for (int[] row : memo) Arrays.fill(row, -1); // -1 marks "not computed yet"

        return funcMemo(1, c, padded, memo);
    }

    private int funcMemo(int i, int j, int[] cuts, int[][] memo) {
        if (i > j) return 0;
        if (memo[i][j] != -1) return memo[i][j];

        int pieceLength = cuts[j + 1] - cuts[i - 1];
        int mini = Integer.MAX_VALUE;
        for (int k = i; k <= j; k++) {
            int ans = pieceLength
                    + funcMemo(i, k - 1, cuts, memo)
                    + funcMemo(k + 1, j, cuts, memo);
            mini = Math.min(mini, ans);
        }
        return memo[i][j] = mini;
    }

    /* ---------- 3. Shared helper ---------- */

    /** Sorted cut positions with sentinels 0 and n added at the two ends. */
    private int[] pad(int n, List<Integer> cuts) {
        int c = cuts.size();
        int[] padded = new int[c + 2];
        padded[0] = 0;
        for (int i = 0; i < c; i++) {
            padded[i + 1] = cuts.get(i);
        }
        padded[c + 1] = n;
        Arrays.sort(padded); // sorting is what makes "cuts in a piece" a contiguous range
        return padded;
    }

    /* ---------- 4. Driver ---------- */

    private static void check(MinimumCostToCutTheStick sol, int n,
                              List<Integer> cuts, int expected) {
        System.out.println("n = " + n + ", cuts = " + cuts
                + "   recursion = " + sol.minCost(n, cuts)
                + "   memo = " + sol.minCostMemo(n, cuts)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        MinimumCostToCutTheStick sol = new MinimumCostToCutTheStick();

        check(sol, 7, new ArrayList<>(List.of(3, 5, 1, 4)), 16); // typical, unsorted input
        check(sol, 9, new ArrayList<>(List.of(5, 6, 1, 4, 2)), 22); // LeetCode sample
        check(sol, 5, new ArrayList<>(List.of(2)), 5);              // edge: a single cut
    }
}
