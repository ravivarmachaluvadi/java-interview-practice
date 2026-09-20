/*
 * =====================================================================
 *  Open the Lock                                   LeetCode 752 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   A lock has 4 wheels, each showing a digit 0-9; one move turns one wheel
 *   one step (9 wraps to 0, 0 wraps to 9). The lock starts at "0000".
 *   Some codes are "deadends": if the lock ever displays one it jams forever.
 *   Return the fewest moves to reach target, or -1 if it cannot be reached.
 *
 * EXAMPLE
 *   deadends = ["0201","0101","0102","1212","2002"], target "0202"  ->  6
 *   deadends = ["8888"], target "0009"                              ->  1
 *   deadends = ["8887","8889","8878","8898","8788","8988","7888","9888"],
 *   target "8888"                                                   -> -1  (target walled in)
 *   deadends = ["0000"], target "8888"                              -> -1  (start is dead)
 *   deadends = [], target "0000"                                    ->  0  (already there)
 *
 * APPROACH  (BFS over an implicit state graph)
 *   1. A node is a 4-digit string, so there are 10^4 = 10000 nodes. The edges
 *      are never given to us: we generate the 8 neighbours of a state on demand.
 *   2. Put the deadends in a HashSet. If "0000" is a deadend, answer -1 at once;
 *      if target is "0000", answer 0 at once.
 *   3. BFS level by level from "0000". Each completed level = one move, so keep
 *      a `moves` counter that increments once per level, not once per node.
 *   4. For each neighbour: skip it if visited or dead; if it equals target,
 *      return the current `moves`; otherwise mark visited AT ENQUEUE TIME and
 *      push it.
 *   5. Queue drains without hitting target -> unreachable, return -1.
 *
 * KEY INSIGHT
 *   BFS does not need an adjacency list. When the states are enumerable and the
 *   neighbour rule is a cheap function (here: turn one wheel one notch), the
 *   graph is implicit - you generate edges while you walk. Every edge costs 1
 *   move, so plain BFS, not Dijkstra, gives the shortest answer. Marking
 *   visited when you ENQUEUE (not when you dequeue) is what keeps each state
 *   out of the queue more than once; forget it and the queue blows up.
 *   Same shape as Word Ladder and Minimum Multiplications.
 *
 * COMPLEXITY
 *   Time  O(10^4 * 8 * 4)  10000 states, 8 neighbours each, 4 chars to build
 *   Space O(10^4)          visited set + queue + deadend set
 *
 * INTERVIEW FOLLOW-UPS
 *   - Bidirectional BFS from start and target: roughly halves the frontier.
 *   - Generalise to L wheels and B digits per wheel: states become B^L.
 *   - Encode a state as an int 0..9999 to swap the HashSet for a boolean[10000].
 *   - What if turning different wheels had different costs? Then it is Dijkstra.
 *
 * RUN
 *   main() runs 5 cases: typical, one-move, target sealed off, dead start,
 *   and start == target. Each line prints actual vs expected.
 */

import java.util.*;

class OpenTheLock {

    private static final String START = "0000";

    public static int openLock(String[] deadends, String target) {
        Set<String> dead = new HashSet<>(Arrays.asList(deadends));

        // Two cases BFS would otherwise get wrong: we can never leave the start,
        // and we are already standing on the answer.
        if (dead.contains(START)) return -1;
        if (START.equals(target)) return 0;

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.offer(START);
        visited.add(START);

        int moves = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();   // freeze the level before draining it
            moves++;                   // everything produced below is `moves` turns away
            for (int i = 0; i < size; i++) {
                String curr = queue.poll();

                for (String next : nextStates(curr)) {
                    if (visited.contains(next) || dead.contains(next)) continue;
                    if (next.equals(target)) return moves;
                    visited.add(next);  // mark at enqueue time, not at dequeue time
                    queue.offer(next);
                }
            }
        }

        return -1;
    }

    /** The 8 states one wheel-turn away: each of the 4 wheels, forward and backward. */
    private static List<String> nextStates(String state) {
        List<String> res = new ArrayList<>(8);
        char[] chars = state.toCharArray();

        for (int i = 0; i < 4; i++) {
            char original = chars[i];

            chars[i] = original == '9' ? '0' : (char) (original + 1);  // forward, 9 wraps to 0
            res.add(new String(chars));

            chars[i] = original == '0' ? '9' : (char) (original - 1);  // backward, 0 wraps to 9
            res.add(new String(chars));

            chars[i] = original;  // restore before moving to the next wheel
        }
        return res;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("typical",
                openLock(new String[]{"0201", "0101", "0102", "1212", "2002"}, "0202"), 6);

        print("one move",
                openLock(new String[]{"8888"}, "0009"), 1);

        print("target sealed in",
                openLock(new String[]{"8887", "8889", "8878", "8898",
                        "8788", "8988", "7888", "9888"}, "8888"), -1);

        print("start is a deadend",
                openLock(new String[]{"0000"}, "8888"), -1);

        print("already at target",
                openLock(new String[]{}, "0000"), 0);
    }
}
