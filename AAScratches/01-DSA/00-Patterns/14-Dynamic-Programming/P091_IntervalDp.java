/*
 * =====================================================================
 *  P091 Interval DP (Choose the Split Point)   Canonical LC 312 | Hard
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 312, Burst Balloons)
 *   Bursting balloon i earns nums[left] * nums[i] * nums[right], where left and right are
 *   its CURRENT neighbours (1 beyond the ends). Return the most coins you can collect.
 *
 * EXAMPLE
 *   [3, 1, 5, 8]  ->  167     burst 1, 5, 3, 8: 15 + 120 + 24 + 8
 *   [1, 5]        ->  10
 *
 * RECOGNIZE WHEN
 *   - The answer for a range [i, j] is built by choosing one split point k inside it and
 *     combining [i, k] and [k, j]: matrix chain order, cutting a stick, merging stones,
 *     triangulating a polygon, bursting balloons.
 *   - Two players take from the ENDS of an array (stone game, predict the winner).
 *   - n is small (<= ~500), so O(n^3) is fine.
 *   Not this if: the split is only at the end of a prefix -> P083_StringPrefixDp (O(n^2) or
 *   less).
 *
 * TEMPLATE
 *   dp[i][j] = best for the range i..j
 *   for len in 2..n:                              // short ranges first
 *       for i in 0..n-len: j = i + len (- 1)
 *           dp[i][j] = best over k in (i, j) of dp[i][k] + dp[k][j] + cost(i, k, j)
 *   answer = dp[0][n-1]
 *
 * APPROACH
 *   1. Pad nums with 1 at both ends. dp[i][j] = best coins from bursting everything
 *      strictly between i and j.
 *   2. Choose k = the LAST balloon burst inside (i, j): its neighbours at that moment are i
 *      and j, so it earns nums[i] * nums[k] * nums[j].
 *   3. Fill by increasing range length.
 *
 * KEY INSIGHT
 *   Pick the element that acts LAST (or the first cut) inside the range: then the two
 *   sides become independent subproblems. Thinking "which balloon bursts first" fails,
 *   because its neighbours change; "which bursts last" fixes them as the range ends.
 *
 * COMPLEXITY
 *   Time O(n^3), space O(n^2).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] Matrix Chain Multiplication      dp[i][j] = min over k of dp[i][k] + dp[k][j]
 *                                            + d[i] * d[k] * d[j]
 *   [coded] LC 1547 Min Cost to Cut a Stick  sort cuts, add 0 and n; cost = cuts[j] - cuts[i]
 *   [coded] LC 1039 Min Score Triangulation  triangle (i, k, j) with weight v[i] * v[k] * v[j]
 *           LC 877  Stone Game               dp[i][j] = max(p[i] - dp[i+1][j], p[j] - dp[i][j-1])
 *           LC 486  Predict the Winner       LC 877 for any array; first player wins if >= 0
 *           LC 1000 Merge Stones             k-way merges: extra dimension or mod check
 *           LC 516  Longest Palindromic Subseq  interval form of P087_TwoStringDp
 *
 * PITFALLS
 *   - Iterate by LENGTH, not by i then j, so smaller ranges are ready.
 *   - Off-by-one in "exclusive" (i, j) ranges: k runs strictly between them.
 *   - Padding (1s for balloons, 0 and n for the stick) removes edge cases.
 *
 * DEEP DIVE
 *   C16_MCM, D01_MinimumCostToCutTheStick (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class IntervalDp {

    // Canonical LC 312.
    static int maxCoins(int[] nums) {
        int n = nums.length + 2;
        int[] v = new int[n];
        v[0] = 1;
        v[n - 1] = 1;
        System.arraycopy(nums, 0, v, 1, nums.length);
        int[][] dp = new int[n][n];
        for (int len = 2; len < n; len++) {
            for (int i = 0; i + len < n; i++) {
                int j = i + len;
                for (int k = i + 1; k < j; k++) {  // k is burst LAST inside (i, j)
                    dp[i][j] = Math.max(dp[i][j], dp[i][k] + dp[k][j] + v[i] * v[k] * v[j]);
                }
            }
        }
        return dp[0][n - 1];
    }

    // Matrix i has size d[i] x d[i + 1]; fewest scalar multiplications.
    static int matrixChainOrder(int[] d) {
        int n = d.length;
        int[][] dp = new int[n][n];
        for (int len = 2; len < n; len++) {
            for (int i = 0; i + len < n; i++) {
                int j = i + len;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i + 1; k < j; k++) {
                    dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k][j] + d[i] * d[k] * d[j]);
                }
            }
        }
        return dp[0][n - 1];
    }

    // LC 1547: each cut costs the current stick length.
    static int minCost(int n, int[] cuts) {
        int[] c = new int[cuts.length + 2];
        System.arraycopy(cuts, 0, c, 1, cuts.length);
        c[c.length - 1] = n;
        Arrays.sort(c);
        int m = c.length;
        int[][] dp = new int[m][m];
        for (int len = 2; len < m; len++) {
            for (int i = 0; i + len < m; i++) {
                int j = i + len;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i + 1; k < j; k++) {  // k is the FIRST cut inside (i, j)
                    dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k][j] + c[j] - c[i]);
                }
            }
        }
        return dp[0][m - 1];
    }

    // LC 1039: polygon vertices in order; triangle (i, k, j) scores v[i] * v[k] * v[j].
    static int minScoreTriangulation(int[] v) {
        int n = v.length;
        int[][] dp = new int[n][n];
        for (int len = 2; len < n; len++) {
            for (int i = 0; i + len < n; i++) {
                int j = i + len;
                dp[i][j] = Integer.MAX_VALUE;
                for (int k = i + 1; k < j; k++) {
                    dp[i][j] = Math.min(dp[i][j], dp[i][k] + dp[k][j] + v[i] * v[k] * v[j]);
                }
            }
        }
        return dp[0][n - 1];
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 312 [3,1,5,8]", maxCoins(new int[]{3, 1, 5, 8}), 167);
        check("LC 312 [1,5]", maxCoins(new int[]{1, 5}), 10);
        check("LC 312 single", maxCoins(new int[]{7}), 7);

        check("MCM [40,20,30,10,30]", matrixChainOrder(new int[]{40, 20, 30, 10, 30}), 26000);
        check("MCM [10,20,30]", matrixChainOrder(new int[]{10, 20, 30}), 6000);
        check("MCM [1,2,3,4,3]", matrixChainOrder(new int[]{1, 2, 3, 4, 3}), 30);

        check("LC 1547 n=7", minCost(7, new int[]{1, 3, 4, 5}), 16);
        check("LC 1547 n=9", minCost(9, new int[]{5, 6, 1, 4, 2}), 22);

        check("LC 1039 triangle", minScoreTriangulation(new int[]{1, 2, 3}), 6);
        check("LC 1039 square", minScoreTriangulation(new int[]{3, 7, 4, 5}), 144);
        check("LC 1039 hexagon", minScoreTriangulation(new int[]{1, 3, 1, 4, 1, 5}), 13);
    }
}
