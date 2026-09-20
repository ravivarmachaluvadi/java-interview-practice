/*
 * =====================================================================
 *  Find All People With the Secret                 LeetCode 2092 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   n people are numbered 0..n-1. Person 0 holds a secret and shares it with
 *   firstPerson at time 0. meetings[i] = {x, y, time} means x and y meet at
 *   that time; if either one already knows the secret at that moment, the other
 *   learns it instantly, and the secret keeps spreading through every meeting
 *   happening at that same timestamp. Return everyone who ends up knowing, in
 *   any order. Times are not sorted in the input and may repeat.
 *
 *   Fixed: the previous version ran one plain BFS over every meeting and threw
 *   the timestamps away, so it let the secret travel backwards in time. See the
 *   "learned too late" case in main(), where it wrongly reported person 2.
 *
 * EXAMPLE
 *   n=6, meetings={{1,2,5},{2,3,8},{1,5,10}}, firstPerson=1  ->  [0,1,2,3,5]
 *   n=4, meetings={{3,1,3},{1,2,2},{0,3,3}}, firstPerson=3   ->  [0,1,3]
 *     1 and 2 meet at time 2, but 1 only learns at time 3, so 2 never hears it
 *
 * APPROACH  (time-grouped connectivity with union-find plus rollback)
 *   1. Union 0 with firstPerson: both know before any meeting happens.
 *   2. Sort the meetings by time and walk them in blocks of equal time.
 *   3. Within one block, union every meeting pair. A block is simultaneous, so
 *      the secret is allowed to chain freely inside it: if a-b and b-c both
 *      meet at time 7 and a knows, then c knows too.
 *   4. After the block, check every person who took part. Anyone not in the
 *      same component as 0 did not learn anything, so ROLL THEM BACK to being
 *      their own singleton. Otherwise their temporary grouping would still be
 *      standing at a later timestamp and would leak the secret backwards.
 *   5. At the end, collect every person whose root equals the root of 0.
 *
 * KEY INSIGHT
 *   Union-find has no notion of time, so time is imposed from outside in two
 *   moves: process timestamps in order, and undo any merge that did not end up
 *   touching person 0. That rollback is the whole difficulty of the problem -
 *   it is what turns a permanent "these people are related" structure into a
 *   snapshot of "these people are related, right now". Look for this shape
 *   whenever connectivity is only valid within a window rather than forever.
 *
 * COMPLEXITY
 *   Time  O(m log m + m * alpha(n))  the sort dominates; every meeting is
 *                                    unioned once and rolled back at most once
 *   Space O(n + m)                   the DSU arrays plus the sorted meetings
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why can a plain BFS or DFS over all meetings not work here?
 *   - Solve it instead with BFS per timestamp over a small local graph - what
 *     are the trade-offs against DSU with rollback?
 *   - What if meetings arrive as a stream already sorted by time? (Drop the sort.)
 *   - What if the secret took one timestamp to propagate instead of being instant?
 *
 * RUN
 *   main() runs 4 cases (typical, the backwards-in-time trap the old code
 *   failed, a same-timestamp chain, and the no-meetings edge case) and prints
 *   actual vs expected.
 */

import java.util.*;

class FindAllPeopleWithSecret {

    /** Union-find that supports resetting a single node back to a singleton. */
    private static class DSU {
        private final int[] parent;
        private final int[] size;

        DSU(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
                size[i] = 1;
            }
        }

        int find(int x) {
            if (parent[x] == x) return x;
            return parent[x] = find(parent[x]);
        }

        void union(int a, int b) {
            int rootA = find(a), rootB = find(b);
            if (rootA == rootB) return;
            if (size[rootA] < size[rootB]) {
                parent[rootA] = rootB;
                size[rootB] += size[rootA];
            } else {
                parent[rootB] = rootA;
                size[rootA] += size[rootB];
            }
        }

        /* Detach x into a component of its own. The old root keeps a slightly
           inflated size, which only skews balancing - never connectivity. */
        void reset(int x) {
            parent[x] = x;
            size[x] = 1;
        }
    }

    public static List<Integer> findAllPeople(int n, int[][] meetings, int firstPerson) {
        DSU dsu = new DSU(n);
        dsu.union(0, firstPerson);   // both know before time starts

        // sort by time so timestamps are handled oldest first
        int[][] sorted = Arrays.stream(meetings).map(int[]::clone).toArray(int[][]::new);
        Arrays.sort(sorted, Comparator.comparingInt(m -> m[2]));

        int i = 0;
        while (i < sorted.length) {
            int time = sorted[i][2];
            int blockStart = i;

            // everything at this timestamp happens at once, so merge it all
            while (i < sorted.length && sorted[i][2] == time) {
                dsu.union(sorted[i][0], sorted[i][1]);
                i++;
            }

            /* Rollback: anyone in this block who is still not connected to 0
               learned nothing. Leaving them merged would let the secret reach
               them through a LATER meeting of their partner, which is the
               backwards-in-time bug. */
            int secretRoot = dsu.find(0);
            for (int j = blockStart; j < i; j++) {
                if (dsu.find(sorted[j][0]) != secretRoot) dsu.reset(sorted[j][0]);
                if (dsu.find(sorted[j][1]) != secretRoot) dsu.reset(sorted[j][1]);
            }
        }

        List<Integer> knows = new ArrayList<>();
        int secretRoot = dsu.find(0);
        for (int person = 0; person < n; person++) {
            if (dsu.find(person) == secretRoot) knows.add(person);
        }
        return knows;   // already ascending because the scan is ascending
    }

    /* ---------- demo ---------- */
    public static void main(String[] args) {
        // typical: each meeting is at a distinct, increasing time
        print("typical", findAllPeople(6,
                        new int[][]{{1, 2, 5}, {2, 3, 8}, {1, 5, 10}}, 1),
                "[0, 1, 2, 3, 5]");

        // tricky: 1 and 2 meet at time 2, but 1 only learns at time 3.
        // The old time-blind BFS reported 2 here; it must not.
        print("learned too late", findAllPeople(4,
                        new int[][]{{3, 1, 3}, {1, 2, 2}, {0, 3, 3}}, 3),
                "[0, 1, 3]");

        // tricky: 1-2 and 2-3 share time 1, so the secret chains inside the block
        print("same-timestamp chain", findAllPeople(5,
                        new int[][]{{3, 4, 2}, {1, 2, 1}, {2, 3, 1}}, 1),
                "[0, 1, 2, 3, 4]");

        // edge: nobody ever meets, only the two seeded people know
        print("edge: no meetings", findAllPeople(3, new int[0][0], 2), "[0, 2]");
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
