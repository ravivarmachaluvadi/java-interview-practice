/*
 * =====================================================================
 *  Minimum Edge Reversals So Every Node Is Reachable  LeetCode 2858 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   You are given n nodes and n-1 directed edges that form a tree when the
 *   directions are ignored. For every node i, report the minimum number of edges
 *   you would have to flip so that every other node is reachable from i.
 *
 * EXAMPLE
 *   n=4, edges=[[2,0],[2,1],[1,3]]           ->  [1, 1, 0, 2]
 *   n=3, edges=[[1,2],[2,0]]                 ->  [2, 0, 1]
 *   n=5, edges=[[0,1],[2,0],[3,2],[3,4]]     ->  [2, 3, 1, 0, 1]
 *        from node 3 every edge already points away, so the cost is 0
 *
 * APPROACH  (rerooting DP: one DFS to score the root, then O(1) per move)
 *   1. Store each tree edge twice: u -> v with weight 0 (already usable) and
 *      v -> u with weight 1 (would need a flip). Now "cost" is just edge weight.
 *   2. DFS once from node 0 over the undirected tree, recording for every node i
 *        depth[i]        = number of edges on the path 0 -> i
 *        flipsOnPath[i]  = how many of those edges point the wrong way
 *      and accumulating totalFlips = the answer for node 0.
 *   3. Reroot from 0 to i. Only the edges on the path 0 -> i change role:
 *        - the flipsOnPath[i] edges that were wrong are now correct -> subtract
 *        - the depth[i] - flipsOnPath[i] edges that were correct are now wrong
 *          -> add
 *      so answer[i] = totalFlips - flipsOnPath[i] + (depth[i] - flipsOnPath[i]).
 *
 * KEY INSIGHT
 *   Moving the root by one edge changes the answer by exactly +/-1, because that
 *   single edge is the only one whose "points away from the root" status flips.
 *   Chaining that along a path gives the closed form above, which turns n separate
 *   O(n) DFS runs into one DFS plus O(1) arithmetic per node. Recognise rerooting
 *   whenever a tree question asks for an answer "for every node as the root".
 *
 * COMPLEXITY
 *   Time  O(n)   one DFS over 2*(n-1) directed entries, then one pass for answers
 *   Space O(n)   adjacency lists, visited/depth/flip arrays, recursion stack
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rewrite the DFS iteratively: n can reach 1e5 and deep chains blow the stack.
 *   - Do it as the classic two-pass rerooting (down[] then up[]) instead of the
 *     path formula; which generalises better to weighted or max-style answers?
 *   - What breaks if the input is a general graph rather than a tree?
 *   - Return only the best node(s) instead of the full array. (min over answer[])
 *
 * RUN
 *   main() runs 5 cases (two LeetCode samples, a 5-node tree, a single node,
 *   and a 2-node tree) and prints actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class MinimumEdgeReversals {

    private List<int[]>[] graph;   // graph[u] holds {neighbour, 0 = fine | 1 = needs a flip}
    private boolean[] visited;
    private int[] depth;           // edges on the path 0 -> node
    private int[] flipsOnPath;     // wrong-way edges on that same path
    private int totalFlips;        // answer for the DFS root, node 0

    @SuppressWarnings("unchecked")
    public int[] minEdgeReversals(int n, int[][] edges) {
        graph = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            graph[i] = new ArrayList<>();
        }
        // Walking u -> v costs nothing; walking v -> u costs one reversal.
        for (int[] e : edges) {
            graph[e[0]].add(new int[]{e[1], 0});
            graph[e[1]].add(new int[]{e[0], 1});
        }

        visited = new boolean[n];
        depth = new int[n];
        flipsOnPath = new int[n];
        totalFlips = 0;

        dfs(0, 0, 0);

        int[] answer = new int[n];
        for (int i = 0; i < n; i++) {
            int noLongerWrong = flipsOnPath[i];                 // path edges that become correct
            int nowWrong = depth[i] - flipsOnPath[i];           // path edges that become wrong
            answer[i] = totalFlips - noLongerWrong + nowWrong;
        }
        return answer;
    }

    /** Records depth and wrong-way count for every node, measured from the DFS root. */
    private void dfs(int node, int flipsSoFar, int depthSoFar) {
        visited[node] = true;
        depth[node] = depthSoFar;
        flipsOnPath[node] = flipsSoFar;

        for (int[] edge : graph[node]) {
            int neighbour = edge[0], weight = edge[1];
            if (visited[neighbour]) {
                continue; // the tree's other direction, or the parent we came from
            }
            totalFlips += weight; // every wrong-way edge costs node 0 one reversal
            dfs(neighbour, flipsSoFar + weight, depthSoFar + 1);
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        MinimumEdgeReversals solver = new MinimumEdgeReversals();

        int[][] sampleOne = {{2, 0}, {2, 1}, {1, 3}};
        print("case 1 (4-node sample)",
                Arrays.toString(solver.minEdgeReversals(4, sampleOne)), "[1, 1, 0, 2]");

        int[][] sampleTwo = {{1, 2}, {2, 0}};
        print("case 2 (3-node chain) ",
                Arrays.toString(solver.minEdgeReversals(3, sampleTwo)), "[2, 0, 1]");

        // Node 3 is the natural root here: every edge already points away from it.
        int[][] branching = {{0, 1}, {2, 0}, {3, 2}, {3, 4}};
        print("case 3 (5-node tree)  ",
                Arrays.toString(solver.minEdgeReversals(5, branching)), "[2, 3, 1, 0, 1]");

        // Edge case: one node, no edges, nothing to reverse.
        print("case 4 (single node)  ",
                Arrays.toString(solver.minEdgeReversals(1, new int[0][0])), "[0]");

        // Edge case: the smallest tree with an edge, so the two answers differ by 1.
        int[][] twoNodes = {{0, 1}};
        print("case 5 (two nodes)    ",
                Arrays.toString(solver.minEdgeReversals(2, twoNodes)), "[0, 1]");
    }
}
