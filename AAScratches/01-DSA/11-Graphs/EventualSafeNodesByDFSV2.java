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
