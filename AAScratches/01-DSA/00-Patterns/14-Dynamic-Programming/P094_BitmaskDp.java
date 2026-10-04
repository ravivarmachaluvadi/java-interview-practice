/*
 * =====================================================================
 *  P094 Bitmask DP (Subsets as States)   Canonical LC 698 | Medium
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 698, Partition to K Equal Sum Subsets)
 *   Return whether nums can be split into k non-empty groups with equal sums.
 *
 * EXAMPLE
 *   [4,3,2,3,5,2,1], k = 4  ->  true      (5), (1,4), (2,3), (2,3)
 *   [1,2,3,4],       k = 3  ->  false     total 10 is not divisible by 3
 *
 * RECOGNIZE WHEN
 *   - n is SMALL (about 12 to 20) and the state is "which items are already used":
 *     assignment, grouping, visiting every node once (travelling salesman), matching.
 *   - Backtracking over subsets repeats the same "used set" many times.
 *   Not this if: n is large (2^n states will not fit); only the count / sum of used items
 *   matters, not WHICH ones -> a normal knapsack (P084_ZeroOneKnapsack).
 *
 * TEMPLATE
 *   state = bitmask of used items (+ maybe the current node / partial value)
 *   dp[mask] (or dp[mask][last]) built from smaller masks:
 *       for mask in 0..2^n-1: if dp[mask] is valid:
 *           for each i not in mask: dp[mask | 1<<i] = combine(dp[mask], item i)
 *   shortest path over states: BFS on (node, mask) when every step costs 1
 *
 * APPROACH
 *   1. target = total / k. dp[mask] = how full the current group is after using the
 *      numbers in mask, or -1 if those numbers cannot be arranged validly.
 *   2. From a valid mask, add any unused number that still fits in the current group;
 *      the fill wraps to 0 when a group completes.
 *   3. The answer is whether the full mask is reachable.
 *
 * KEY INSIGHT
 *   Many different orders lead to the same SET of used items, and from there the future
 *   depends only on the set (plus a small extra like the current node). Indexing the DP by
 *   a bitmask collapses n! orders into 2^n states.
 *
 * COMPLEXITY
 *   LC 698: O(2^n * n). TSP: O(2^n * n^2). LC 847: O(2^n * n^2) BFS.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] Travelling Salesman (TSP)        dp[mask][last] = cheapest path covering mask
 *                                            and ending at last; close the tour at the end
 *   [coded] LC 847  Visit All Nodes          BFS over (node, mask); start from every node
 *           LC 1986 Min Work Sessions        dp[mask] = (sessions, time left in the last)
 *           LC 526  Beautiful Arrangement    dp[mask]: position = bitCount(mask) + 1
 *           LC 1125 Smallest Sufficient Team mask over SKILLS, not people
 *           LC 464  Can I Win                memoised game over the mask of used numbers
 *           LC 473  Matchsticks to Square    LC 698 with k = 4
 *
 * PITFALLS
 *   - 1 << n overflows int for n >= 31; real limits are n <= 20 or so.
 *   - LC 698: sort or prune (any number > target makes it impossible).
 *   - In a BFS over (node, mask), visited must be per (node, mask), not per node.
 *
 * DEEP DIVE
 *   D07_StickersToSpellWord (12-Dynamic-Programming), C06_MColoringProblem
 *   (14-Backtracking-Recursion)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

class BitmaskDp {

    // Canonical LC 698.
    static boolean canPartitionKSubsets(int[] nums, int k) {
        int total = 0;
        for (int x : nums) {
            total += x;
        }
        if (total % k != 0) {
            return false;
        }
        int target = total / k;
        int n = nums.length;
        int[] fill = new int[1 << n];              // fill of the current group, -1 = unreachable
        Arrays.fill(fill, -1);
        fill[0] = 0;
        for (int mask = 0; mask < (1 << n); mask++) {
            if (fill[mask] < 0) {
                continue;
            }
            for (int i = 0; i < n; i++) {
                int next = mask | (1 << i);
                if (next != mask && fill[next] < 0 && fill[mask] + nums[i] <= target) {
                    fill[next] = (fill[mask] + nums[i]) % target;   // wraps when a group closes
                }
            }
        }
        return fill[(1 << n) - 1] == 0;
    }

    // TSP: shortest tour from city 0 visiting every city once and returning to 0.
    static int tsp(int[][] dist) {
        int n = dist.length;
        final int inf = Integer.MAX_VALUE / 2;
        int[][] dp = new int[1 << n][n];
        for (int[] row : dp) {
            Arrays.fill(row, inf);
        }
        dp[1][0] = 0;                              // only city 0 visited, standing at 0
        for (int mask = 1; mask < (1 << n); mask++) {
            for (int last = 0; last < n; last++) {
                if (dp[mask][last] >= inf || (mask & (1 << last)) == 0) {
                    continue;
                }
                for (int next = 0; next < n; next++) {
                    if ((mask & (1 << next)) == 0) {
                        int m2 = mask | (1 << next);
                        dp[m2][next] = Math.min(dp[m2][next], dp[mask][last] + dist[last][next]);
                    }
                }
            }
        }
        int best = inf;
        for (int last = 0; last < n; last++) {
            best = Math.min(best, dp[(1 << n) - 1][last] + dist[last][0]);
        }
        return best;
    }

    // LC 847: shortest walk (edges may repeat) that visits every node; any start.
    static int shortestPathLength(int[][] graph) {
        int n = graph.length;
        int all = (1 << n) - 1;
        boolean[][] seen = new boolean[n][1 << n];
        Deque<int[]> queue = new ArrayDeque<>();   // {node, mask}
        for (int i = 0; i < n; i++) {
            queue.add(new int[]{i, 1 << i});
            seen[i][1 << i] = true;
        }
        int steps = 0;
        while (!queue.isEmpty()) {
            for (int size = queue.size(); size > 0; size--) {
                int[] cur = queue.poll();
                if (cur[1] == all) {
                    return steps;
                }
                for (int next : graph[cur[0]]) {
                    int mask = cur[1] | (1 << next);
                    if (!seen[next][mask]) {
                        seen[next][mask] = true;
                        queue.add(new int[]{next, mask});
                    }
                }
            }
            steps++;
        }
        return -1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 698 k=4", canPartitionKSubsets(new int[]{4, 3, 2, 3, 5, 2, 1}, 4), true);
        check("LC 698 total not divisible", canPartitionKSubsets(new int[]{1, 2, 3, 4}, 3), false);
        check("LC 698 divisible but impossible",
                canPartitionKSubsets(new int[]{2, 2, 2, 2, 3, 4, 5}, 4), false);
        check("LC 698 k=1", canPartitionKSubsets(new int[]{5, 1}, 1), true);

        int[][] dist = {{0, 10, 15, 20}, {10, 0, 35, 25}, {15, 35, 0, 30}, {20, 25, 30, 0}};
        check("TSP four cities", tsp(dist), 80);
        check("TSP two cities", tsp(new int[][]{{0, 7}, {7, 0}}), 14);

        check("LC 847 star", shortestPathLength(new int[][]{{1, 2, 3}, {0}, {0}, {0}}), 4);
        check("LC 847 five nodes",
                shortestPathLength(new int[][]{{1}, {0, 2, 4}, {1, 3, 4}, {2}, {1, 2}}), 4);
        check("LC 847 single node", shortestPathLength(new int[][]{{}}), 0);
    }
}
