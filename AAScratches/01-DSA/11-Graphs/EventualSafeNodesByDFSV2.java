/**
 * Problem: Given a directed graph represented as an adjacency list (int[][]), find all nodes that are "eventually safe".
 * A node is eventually safe if every possible path starting from it leads to a terminal node (no outgoing edges) and never enters a cycle.
 *
 * Approach: Perform depth‑first search with memoization. Each node can be in one of three states:
 * 0 = unvisited, 1 = currently visiting (in recursion stack), 2 = safe. During DFS, if we encounter a node already marked as unsafe or revisit a node in state 1, the current path leads to a cycle and is unsafe.
 * After exploring all neighbors successfully, mark the node as safe (state 2) and add it to the result list.
 *
 * Time Complexity: O(V + E), where V is number of nodes and E is total edges, since each edge and node is processed at most once.
 * Space Complexity: O(V) for recursion stack and state array; additional O(V) for the output list. */
import java.util.*;

public class EventualSafeNodesByDFSV2 {
    public List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        int[] state = new int[n]; // 0 = unvisited, 1 = visiting, 2 = safe
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (dfs(i, graph, state)) {
                result.add(i);
            }
        }
        return result;
    }

    private boolean dfs(int node, int[][] graph, int[] state) {
        if (state[node] != 0) {
            return state[node] == 2; // already determined safe or unsafe
        }
        state[node] = 1; // visiting
        for (int nei : graph[node]) {
            if (!dfs(nei, graph, state)) {
                return false; // leads to cycle
            }
        }
        state[node] = 2; // safe
        return true;
    }

    public static void main(String[] args) {
        EventualSafeNodesByDFSV2 obj = new EventualSafeNodesByDFSV2();
        int[][] graph = {{1, 2}, {2, 3}, {5}, {0}, {5}, {}, {}};
        System.out.println(obj.eventualSafeNodes(graph)); // [2,4,5,6]
    }
}
