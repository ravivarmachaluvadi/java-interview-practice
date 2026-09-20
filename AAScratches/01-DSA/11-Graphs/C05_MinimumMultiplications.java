/*
 * =====================================================================
 *  Minimum Multiplications to Reach End           GFG / Striver | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an array arr, a start value and an end value (both in [0, 99999]),
 *   repeatedly replace the current value v by (v * arr[i]) % 100000 for any i
 *   you like. Return the fewest such steps to turn start into end, or -1 if end
 *   can never be produced.
 *
 * EXAMPLE
 *   arr = [2, 5, 7],  start = 3,     end = 30      ->  2   (3*2=6, 6*5=30)
 *   arr = [3, 4, 65], start = 7,     end = 66175   ->  4
 *   arr = [10],       start = 3,     end = 7       -> -1   (3,30,300,3000,30000,0,0...)
 *   arr = [99999],    start = 99999, end = 1       ->  1   (99999*99999 % 100000 == 1)
 *   arr = [2, 5, 7],  start = 3,     end = 3       ->  0   (nothing to do)
 *
 * APPROACH  (BFS over the 100000 residues)
 *   1. Reframe it as a graph: every remainder 0..99999 is a node. From node v
 *      there is an edge to (v * arr[i]) % 100000 for each i, and every edge
 *      costs exactly one multiplication.
 *   2. Equal edge weights means plain BFS, not Dijkstra, finds the minimum.
 *   3. Seed the queue with (steps = 0, value = start), with minSteps[start] = 0.
 *   4. Pop (steps, v), generate all |arr| products. Return steps + 1 the moment
 *      a product equals end; otherwise push it only if steps + 1 improves
 *      minSteps, which is what stops the walk from revisiting a residue.
 *   5. Queue empties without reaching end -> end is unreachable, return -1.
 *
 * KEY INSIGHT
 *   "Mod 100000" is not an arithmetic detail, it is the bound that makes the
 *   state space finite: there are only 100000 distinct values, so the search
 *   must terminate. Recognise the pattern - an implicit graph whose nodes are
 *   generated states (words in Word Ladder, lock codes in Open the Lock,
 *   residues here). The recipe is always the same: finite state space + unit
 *   cost transition = BFS with a visited/dist array.
 *
 * COMPLEXITY
 *   Time  O(100000 * |arr|)  each residue is expanded at most once, |arr| edges
 *   Space O(100000)          the minSteps array plus the queue
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not DFS? DFS finds *a* chain, not the shortest one.
 *   - Why not Dijkstra? All edges cost 1, so BFS already yields optimal order.
 *   - Bidirectional BFS: hard here, because multiplication mod 100000 is not
 *     invertible when arr[i] shares a factor with 100000.
 *   - If each arr[i] had its own cost, this becomes Dijkstra over the residues.
 *
 * FIXED
 *   The original computed (val * arr[i]) % mod in int arithmetic. With
 *   val and arr[i] up to 99999 the product reaches ~1e10, which overflows int
 *   and silently produces a wrong (even negative) residue. Promoting the
 *   multiply to long fixes it; the "arr = [99999]" case below is the proof.
 *
 * RUN
 *   main() runs 5 cases: typical, longer chain, unreachable, the overflow
 *   case, and start == end. Each line prints actual vs expected.
 */

import java.util.*;

class MinimumMultiplications {

    private static final int MOD = 100_000;

    public int minimumMultiplications(int[] arr, int start, int end) {
        if (start == end) return 0;

        int[] minSteps = new int[MOD];
        Arrays.fill(minSteps, Integer.MAX_VALUE);

        Queue<int[]> q = new LinkedList<>();   // each entry is {stepsSoFar, value}
        minSteps[start] = 0;
        q.add(new int[]{0, start});

        while (!q.isEmpty()) {
            int[] node = q.poll();
            int steps = node[0];
            int val = node[1];

            for (int factor : arr) {
                // long multiply: val * factor can be ~1e10, far past Integer.MAX_VALUE
                int next = (int) (((long) val * factor) % MOD);

                if (next == end) return steps + 1;   // BFS order, so this is the minimum

                if (steps + 1 < minSteps[next]) {    // first time we reach this residue
                    minSteps[next] = steps + 1;
                    q.add(new int[]{steps + 1, next});
                }
            }
        }
        return -1;   // every reachable residue explored and end was not among them
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        MinimumMultiplications sol = new MinimumMultiplications();

        print("typical",
                sol.minimumMultiplications(new int[]{2, 5, 7}, 3, 30), 2);

        print("longer chain",
                sol.minimumMultiplications(new int[]{3, 4, 65}, 7, 66175), 4);

        print("unreachable",
                sol.minimumMultiplications(new int[]{10}, 3, 7), -1);

        print("overflow guard",
                sol.minimumMultiplications(new int[]{99999}, 99999, 1), 1);

        print("start == end",
                sol.minimumMultiplications(new int[]{2, 5, 7}, 3, 3), 0);
    }
}
