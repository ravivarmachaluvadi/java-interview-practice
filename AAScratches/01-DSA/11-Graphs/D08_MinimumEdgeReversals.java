import java.util.*;

// https://leetcode.com/problems/minimum-edge-reversals-so-every-node-is-reachable/description/
// 2858. Minimum Edge Reversals So Every Node Is Reachable
class MinimumEdgeReversals {
    private void dfs(int node, int reversals, int moves, int tot_rev[],
                     List<int[]>[] g, boolean[] vis, int[] cost, int[] depth) {
        depth[node] = moves;
        cost[node] = reversals;
        vis[node] = true;

        for (int[] pot : g[node]) {
            int neighbour = pot[0], wt = pot[1];
            if (vis[neighbour]) continue;
            int next_moves = moves + 1;

            if (wt == 0) {
                dfs(neighbour, reversals, next_moves, tot_rev, g, vis, cost, depth);
            } else {
                tot_rev[0]++;
                dfs(neighbour, reversals + 1, next_moves, tot_rev, g, vis, cost, depth);
            }
        }
    }

    public int[] minEdgeReversals(int n, int[][] edges) {
        List<int[]>[] g = new ArrayList[n];
        for (int i = 0; i < n; i++) g[i] = new ArrayList<>();

        // Build directed + reverse edge graph
        for (int[] e : edges) {
            g[e[0]].add(new int[]{e[1], 0}); // 0 = no reversal needed
            g[e[1]].add(new int[]{e[0], 1}); // 1 = reversal needed
        }

        boolean[] vis = new boolean[n];
        int[] cost = new int[n];
        int[] depth = new int[n];
        Arrays.fill(cost, -1);
        Arrays.fill(depth, -1);

        int[] tot_rev = new int[]{0};
        dfs(0, 0, 0, tot_rev, g, vis, cost, depth);

        int[] ans = new int[n];
        for (int i = 0; i < n; i++) {
            int count_excluding_till_i = (tot_rev[0] - cost[i]);
            int rev_count_from_i_to_0 = (depth[i] - cost[i]);
            ans[i] = count_excluding_till_i + rev_count_from_i_to_0;
        }

        return ans;
    }

    // ---------------- Main Method for Testing ----------------
    public static void main(String[] args) {
        MinimumEdgeReversals sol = new MinimumEdgeReversals();

        int n = 5;
        int[][] edges = {
                {0, 1},
                {2, 0},
                {3, 2},
                {3, 4}
        };

        int[] result = sol.minEdgeReversals(n, edges);
        System.out.println("Minimum edge reversals for each node: " + Arrays.toString(result));
    }
}
