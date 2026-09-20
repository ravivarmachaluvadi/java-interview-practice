/*
 * =====================================================================
 *  Keys and Rooms                                    LeetCode 841 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   There are n rooms numbered 0..n-1. All are locked except room 0, and
 *   rooms.get(i) is the list of keys lying inside room i. Starting in room 0
 *   and picking up every key you find, can you eventually enter every room?
 *
 * EXAMPLE
 *   rooms = [[1], [2], [3], []]        ->  true   0 -> 1 -> 2 -> 3
 *   rooms = [[1, 3], [3, 0, 1], [2], [0]] -> false  no key to room 2 exists
 *   rooms = [[]]                       ->  true   the only room is the start
 *
 * APPROACH  (reachability from a single source)
 *   1. The rooms are nodes; a key in room i pointing at room k is a directed
 *      edge i -> k. So "can I open everything" is "is every node reachable
 *      from node 0".
 *   2. Run one DFS from room 0, marking visited[room] = true on entry.
 *   3. Recurse into every key you have not used yet.
 *   4. After the single DFS finishes, scan visited[]. One false means some
 *      room is unreachable, so return false.
 *
 * KEY INSIGHT
 *   Nothing here needs a second traversal or a counter reset: a single DFS
 *   from the source paints exactly the reachable set, and the answer is just
 *   "did the paint cover everything". Recognise this shape whenever a problem
 *   says "starting at X, can you get to all of Y" - it is one traversal plus a
 *   completeness check, never a search per target.
 *
 * COMPLEXITY
 *   Time  O(V + E)  every room entered once, every key inspected once
 *   Space O(V)      visited array plus recursion stack (or explicit stack)
 *
 * INTERVIEW FOLLOW-UPS
 *   - n can be 100000 and the graph is a path: recursion stack overflows.
 *     Show the iterative version (canVisitAllRoomsIterative below).
 *   - Return WHICH rooms are unreachable instead of a boolean.
 *   - Undirected version: then connectivity is symmetric and union-find works.
 *   - What if keys can be used only once? That becomes a matching problem,
 *     not plain reachability.
 *
 * RUN
 *   main() runs 4 cases (typical chain, unreachable room, single room,
 *   self-loop) through both the recursive and the iterative solver and prints
 *   actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

class KeysAndRooms {

    /** Recursive DFS: paint everything reachable from room 0, then check coverage. */
    public boolean canVisitAllRooms(List<List<Integer>> rooms) {
        boolean[] visited = new boolean[rooms.size()];
        dfs(rooms, 0, visited);

        for (boolean roomVisited : visited) {
            if (!roomVisited) return false; // some room had no key chain leading to it
        }
        return true;
    }

    private void dfs(List<List<Integer>> rooms, int room, boolean[] visited) {
        visited[room] = true;
        for (int key : rooms.get(room)) {
            if (!visited[key]) {
                dfs(rooms, key, visited);
            }
        }
    }

    /**
     * Same traversal with an explicit stack. Identical answer, but safe when the
     * key chain is long enough to blow the JVM's recursion depth.
     */
    public boolean canVisitAllRoomsIterative(List<List<Integer>> rooms) {
        boolean[] visited = new boolean[rooms.size()];
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(0);
        visited[0] = true;
        int opened = 1; // count as we go so no final scan is needed

        while (!stack.isEmpty()) {
            int room = stack.pop();
            for (int key : rooms.get(room)) {
                if (!visited[key]) {
                    visited[key] = true; // mark at push time: never queue a room twice
                    opened++;
                    stack.push(key);
                }
            }
        }
        return opened == rooms.size();
    }

    private static void print(String label, List<List<Integer>> rooms, boolean expected) {
        KeysAndRooms solution = new KeysAndRooms();
        boolean recursive = solution.canVisitAllRooms(rooms);
        boolean iterative = solution.canVisitAllRoomsIterative(rooms);
        System.out.println(label + ": recursive=" + recursive + " iterative=" + iterative
                + "   expected " + expected + " " + expected);
    }

    public static void main(String[] args) {
        // case 1: a straight chain of keys, every room reachable
        print("case 1 chain [[1],[2],[3],[]]",
                List.of(List.of(1), List.of(2), List.of(3), List.of()), true);

        // case 2: room 2 holds the only key to itself, so it can never be opened
        print("case 2 orphan [[1,3],[3,0,1],[2],[0]]",
                List.of(List.of(1, 3), List.of(3, 0, 1), List.of(2), List.of(0)), false);

        // case 3 (edge): a single empty room - you are already inside it
        print("case 3 single [[]]", List.of(List.of()), true);

        // case 4 (tricky): duplicate and self keys must not cause infinite recursion
        print("case 4 self+dup [[0,1,1],[0]]", List.of(List.of(0, 1, 1), List.of(0)), true);
    }
}
