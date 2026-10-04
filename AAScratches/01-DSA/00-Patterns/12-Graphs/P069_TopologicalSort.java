/*
 * =====================================================================
 *  P069 Topological Sort (Kahn's BFS)   Canonical LC 210 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 210, Course Schedule II)
 *   numCourses courses; prerequisites [a, b] means b must be taken before a. Return an
 *   order that takes every course, or [] if it is impossible (a cycle).
 *
 * EXAMPLE
 *   2, [[1,0]]                       ->  [0, 1]
 *   4, [[1,0],[2,0],[3,1],[3,2]]     ->  [0, 1, 2, 3]   (also [0, 2, 1, 3])
 *   2, [[1,0],[0,1]]                 ->  []             cycle
 *
 * RECOGNIZE WHEN
 *   - "prerequisites", "dependencies", "build order", "must come before", "compile order".
 *   - Infer an order from pairwise rules (alien dictionary).
 *   - "can it be done at all?" = "is the dependency graph acyclic?"
 *   - Minimum number of rounds / semesters = number of BFS levels.
 *   Not this if: the graph is undirected -> components (P066_AdjacencyComponents) or cycle
 *   via union-find (P071_UnionFind).
 *
 * TEMPLATE
 *   build adj (before -> after) and indegree[]
 *   queue = every node with indegree 0
 *   while queue:
 *       u = queue.poll(); order.add(u)
 *       for v in adj[u]: if --indegree[v] == 0: queue.add(v)
 *   order.size < n  ->  a cycle exists
 *   DFS alternative: post-order with 3 colours, then reverse
 *
 * APPROACH
 *   1. A course with no remaining prerequisites can be taken now.
 *   2. Taking it removes one prerequisite from each course that depends on it.
 *   3. If some courses never reach in-degree 0, they sit on a cycle.
 *
 * KEY INSIGHT
 *   A DAG always has at least one node with no incoming edges; removing it leaves a DAG.
 *   Repeating that peels the graph in a valid order, and getting stuck before all nodes are
 *   peeled is exactly the proof that a cycle exists.
 *
 * COMPLEXITY
 *   Time O(V + E), space O(V + E).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 207  Course Schedule          only the count matters: order.size == n
 *   [coded] LC 269  Alien Dictionary         edges from the first differing letter of each
 *                                            adjacent word pair; "abc" before "ab" is invalid
 *   [coded] LC 802  Eventual Safe States     reverse the edges; Kahn from terminal nodes
 *   [coded] LC 1136 Parallel Courses         count BFS levels = minimum semesters
 *           LC 2115 Find All Recipes         ingredients -> recipes; supplies start at 0
 *           LC 310  Minimum Height Trees     peel LEAVES layer by layer (undirected Kahn)
 *           LC 444  Sequence Reconstruction  unique order iff the queue never holds 2 nodes
 *           LC 2050 Parallel Courses III     longest path in the DAG (time per course)
 *
 * PITFALLS
 *   - Edge direction: [a, b] with "b before a" means b -> a.
 *   - LC 269: a longer word before its own prefix ("abc", "ab") makes the order impossible.
 *   - Duplicate edges inflate in-degrees: dedupe (LC 269) or count them consistently.
 *
 * DEEP DIVE
 *   B10_CourseSchedule, A04_ToposortDFS, D02_AlienDictionaryOrder,
 *   C06_EventualSafeNodesByDFS, C07_ParallelCourses (11-Graphs)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

class TopologicalSort {

    // Canonical LC 210.
    static int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }
        int[] indegree = new int[numCourses];
        for (int[] p : prerequisites) {
            adj.get(p[1]).add(p[0]);               // take p[1] before p[0]
            indegree[p[0]]++;
        }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }
        int[] order = new int[numCourses];
        int taken = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            order[taken++] = u;
            for (int v : adj.get(u)) {
                if (--indegree[v] == 0) {
                    queue.add(v);
                }
            }
        }
        return taken == numCourses ? order : new int[0];
    }

    // LC 207.
    static boolean canFinish(int numCourses, int[][] prerequisites) {
        return findOrder(numCourses, prerequisites).length == numCourses;
    }

    // LC 269: a min-heap picks the smallest letter among the ready ones, for a stable output.
    static String alienOrder(String[] words) {
        boolean[] present = new boolean[26];
        for (String w : words) {
            for (char c : w.toCharArray()) {
                present[c - 'a'] = true;
            }
        }
        List<Set<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 26; i++) {
            adj.add(new HashSet<>());
        }
        int[] indegree = new int[26];
        for (int i = 0; i + 1 < words.length; i++) {
            String a = words[i];
            String b = words[i + 1];
            int j = 0;
            while (j < a.length() && j < b.length() && a.charAt(j) == b.charAt(j)) {
                j++;
            }
            if (j == Math.min(a.length(), b.length())) {
                if (a.length() > b.length()) {
                    return "";                     // "abc" listed before its prefix "ab"
                }
                continue;
            }
            if (adj.get(a.charAt(j) - 'a').add(b.charAt(j) - 'a')) {
                indegree[b.charAt(j) - 'a']++;
            }
        }
        PriorityQueue<Integer> ready = new PriorityQueue<>();
        int letters = 0;
        for (int c = 0; c < 26; c++) {
            if (present[c]) {
                letters++;
                if (indegree[c] == 0) {
                    ready.add(c);
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        while (!ready.isEmpty()) {
            int u = ready.poll();
            sb.append((char) ('a' + u));
            for (int v : adj.get(u)) {
                if (--indegree[v] == 0) {
                    ready.add(v);
                }
            }
        }
        return sb.length() == letters ? sb.toString() : "";
    }

    // LC 802: safe = every path ends at a terminal node. Reverse edges; Kahn from terminals.
    static List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        List<List<Integer>> reverse = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            reverse.add(new ArrayList<>());
        }
        int[] outdegree = new int[n];
        for (int u = 0; u < n; u++) {
            outdegree[u] = graph[u].length;
            for (int v : graph[u]) {
                reverse.get(v).add(u);
            }
        }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int u = 0; u < n; u++) {
            if (outdegree[u] == 0) {
                queue.add(u);
            }
        }
        boolean[] safe = new boolean[n];
        while (!queue.isEmpty()) {
            int v = queue.poll();
            safe[v] = true;
            for (int u : reverse.get(v)) {
                if (--outdegree[u] == 0) {
                    queue.add(u);
                }
            }
        }
        List<Integer> out = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            if (safe[u]) {
                out.add(u);
            }
        }
        return out;
    }

    // LC 1136: courses 1..n; minimum semesters, -1 on a cycle.
    static int minimumSemesters(int n, int[][] relations) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i <= n; i++) {
            adj.add(new ArrayList<>());
        }
        int[] indegree = new int[n + 1];
        for (int[] r : relations) {
            adj.get(r[0]).add(r[1]);
            indegree[r[1]]++;
        }
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 1; i <= n; i++) {
            if (indegree[i] == 0) {
                queue.add(i);
            }
        }
        int semesters = 0;
        int taken = 0;
        while (!queue.isEmpty()) {
            semesters++;
            for (int size = queue.size(); size > 0; size--) {
                int u = queue.poll();
                taken++;
                for (int v : adj.get(u)) {
                    if (--indegree[v] == 0) {
                        queue.add(v);
                    }
                }
            }
        }
        return taken == n ? semesters : -1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 210 two courses", Arrays.toString(findOrder(2, new int[][]{{1, 0}})), "[0, 1]");
        check("LC 210 diamond",
                Arrays.toString(findOrder(4, new int[][]{{1, 0}, {2, 0}, {3, 1}, {3, 2}})),
                "[0, 1, 2, 3]");
        check("LC 210 cycle", Arrays.toString(findOrder(2, new int[][]{{1, 0}, {0, 1}})), "[]");
        check("LC 210 no prerequisites", Arrays.toString(findOrder(1, new int[][]{})), "[0]");

        check("LC 207 possible", canFinish(2, new int[][]{{1, 0}}), true);
        check("LC 207 cycle", canFinish(2, new int[][]{{1, 0}, {0, 1}}), false);

        check("LC 269 five words",
                alienOrder(new String[]{"wrt", "wrf", "er", "ett", "rftt"}), "wertf");
        check("LC 269 z before x", alienOrder(new String[]{"z", "x"}), "zx");
        check("LC 269 contradiction", "[" + alienOrder(new String[]{"z", "x", "z"}) + "]", "[]");
        check("LC 269 prefix trap", "[" + alienOrder(new String[]{"abc", "ab"}) + "]", "[]");

        check("LC 802 seven nodes",
                eventualSafeNodes(new int[][]{{1, 2}, {2, 3}, {5}, {0}, {5}, {}, {}}),
                "[2, 4, 5, 6]");
        check("LC 802 one safe",
                eventualSafeNodes(new int[][]{{1, 2, 3, 4}, {1, 2}, {3, 4}, {0, 4}, {}}), "[4]");

        check("LC 1136 two semesters", minimumSemesters(3, new int[][]{{1, 3}, {2, 3}}), 2);
        check("LC 1136 cycle", minimumSemesters(3, new int[][]{{1, 2}, {2, 3}, {3, 1}}), -1);
    }
}
