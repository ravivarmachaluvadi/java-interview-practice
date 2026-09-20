/*
 * =====================================================================
 *  Parallel Courses                               LeetCode 1136 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Courses are numbered 1..n. relations[i] = [prevCourse, nextCourse] means
 *   prevCourse must be finished in an EARLIER semester than nextCourse. In one
 *   semester you may take any number of courses whose prerequisites are all
 *   already done. Return the minimum number of semesters to finish everything,
 *   or -1 if the prerequisites are cyclic and it is impossible.
 *
 * EXAMPLE
 *   n = 4, relations = [[1,2],[1,3],[2,4],[3,4]]  ->  3
 *          semester 1: {1}   semester 2: {2, 3}   semester 3: {4}
 *   n = 2, relations = [[1,2],[2,1]]              -> -1   (cycle)
 *   n = 3, relations = []                         ->  1   (all in one semester)
 *
 * APPROACH  (Kahn's topological sort, processed level by level)
 *   1. Build the adjacency list prev -> next and the in-degree of every course.
 *   2. Seed a queue with every course of in-degree 0: those are semester 1.
 *   3. Loop: snapshot queue.size() FIRST, drain exactly that many courses (one
 *      whole semester), decrement their successors' in-degrees and enqueue any
 *      that hit 0. Those land in the NEXT semester. Count one semester per loop.
 *   4. Track how many courses were taken in total. If that is less than n, some
 *      courses never reached in-degree 0, which means a cycle -> return -1.
 *
 * KEY INSIGHT
 *   The only change from plain Kahn's is freezing the queue size before draining
 *   it: that snapshot is exactly one semester's worth of courses, so the loop
 *   count is the answer. This "level snapshot" is the same trick BFS uses to
 *   count distance, and it answers every "minimum rounds / minimum days /
 *   minimum semesters" follow-up. The taken-count check is not optional - it is
 *   the ONLY thing that separates "finished" from "stuck in a cycle".
 *
 * COMPLEXITY
 *   Time  O(n + E)  every course dequeued once, every relation relaxed once
 *   Space O(n + E)  adjacency list, in-degree array, queue
 *
 * THE k-LIMITED VARIANT  (Parallel Courses II, LeetCode 1494 | Hard)
 *   If you may take AT MOST k courses per semester, the level trick breaks: it
 *   now matters WHICH k ready courses you pick, and that choice is NP-hard.
 *   Both are below so the trap is visible:
 *     minSemestersGreedyLimit  - take the first k in queue order. WRONG.
 *     minSemestersOptimalLimit - bitmask DP over subsets of finished courses.
 *   n = 4, k = 2, relations = [[1,4],[3,4]]: greedy takes {1,2} then {3} then
 *   {4} = 3 semesters; taking {1,3} then {2,4} finishes in 2.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return one valid schedule (the courses per semester), not just the count.
 *   - Same shape as Course Schedule I/II and Minimum Height Trees.
 *   - Longest path in the DAG equals the semester count - why must they match?
 *   - For 1494, why is greedy wrong but the 2^n DP acceptable? Because n <= 15.
 *
 * FIXED
 *   1. No cycle check: the original returned a semester count even when courses
 *      were left unfinished, so a cyclic input answered e.g. 0 instead of -1.
 *   2. The k cap was applied greedily, which does not solve LeetCode 1494; the
 *      greedy is kept but renamed and shown failing next to a correct DP.
 *   3. relations now use the LeetCode order [prevCourse, nextCourse]; the
 *      original read the pair backwards relative to the linked problem.
 *
 * RUN
 *   main() runs 3 cases for the unlimited problem (typical, cycle, no
 *   prerequisites) plus the k-limited counterexample, printing actual vs
 *   expected on each line.
 */

import java.util.*;

class ParallelCourses {

    /** LeetCode 1136: no cap on courses per semester. Returns -1 on a cycle. */
    public static int minimumSemesters(int n, List<List<Integer>> relations) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i <= n; i++) graph.add(new ArrayList<>());   // index 0 unused
        int[] inDegree = new int[n + 1];

        for (List<Integer> relation : relations) {
            int prev = relation.get(0);
            int next = relation.get(1);
            graph.get(prev).add(next);
            inDegree[next]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int course = 1; course <= n; course++) {
            if (inDegree[course] == 0) queue.offer(course);   // semester 1
        }

        int semesters = 0;
        int taken = 0;

        while (!queue.isEmpty()) {
            int thisSemester = queue.size();   // snapshot BEFORE draining: one semester
            for (int i = 0; i < thisSemester; i++) {
                int course = queue.poll();
                taken++;
                for (int next : graph.get(course)) {
                    if (--inDegree[next] == 0) queue.offer(next);   // ready for the next semester
                }
            }
            semesters++;
        }

        // Courses stuck above 0 in-degree sit on a cycle and can never be taken.
        return taken == n ? semesters : -1;
    }

    // ------------------------------------------------------------------
    // Parallel Courses II (LeetCode 1494): at most k courses per semester.
    // ------------------------------------------------------------------

    /**
     * WRONG ON PURPOSE - kept because it is the tempting answer.
     * Takes the first k ready courses in queue order. Optimal scheduling has to
     * choose WHICH k unblock the most work, which FIFO order cannot know.
     */
    public static int minSemestersGreedyLimit(int n, List<List<Integer>> relations, int k) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i <= n; i++) graph.add(new ArrayList<>());
        int[] inDegree = new int[n + 1];

        for (List<Integer> relation : relations) {
            graph.get(relation.get(0)).add(relation.get(1));
            inDegree[relation.get(1)]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int course = 1; course <= n; course++) {
            if (inDegree[course] == 0) queue.offer(course);
        }

        int semesters = 0;
        int taken = 0;

        while (!queue.isEmpty()) {
            int toTake = Math.min(queue.size(), k);   // the arbitrary choice that costs optimality
            for (int i = 0; i < toTake; i++) {
                int course = queue.poll();
                taken++;
                for (int next : graph.get(course)) {
                    if (--inDegree[next] == 0) queue.offer(next);
                }
            }
            semesters++;
        }
        return taken == n ? semesters : -1;
    }

    /**
     * Correct LeetCode 1494: dp[mask] = fewest semesters to finish exactly the
     * courses in mask. From a mask, the available courses are the unfinished
     * ones whose prerequisite mask is already contained in mask; try every
     * k-sized subset of them. Feasible only because n <= 15 there.
     */
    public static int minSemestersOptimalLimit(int n, List<List<Integer>> relations, int k) {
        int[] prereq = new int[n];   // bit i set means course i+1 is a prerequisite
        for (List<Integer> relation : relations) {
            prereq[relation.get(1) - 1] |= 1 << (relation.get(0) - 1);
        }

        int full = (1 << n) - 1;
        int unreachable = Integer.MAX_VALUE / 2;
        int[] dp = new int[1 << n];
        Arrays.fill(dp, unreachable);
        dp[0] = 0;

        for (int mask = 0; mask <= full; mask++) {
            if (dp[mask] >= unreachable) continue;   // this set of courses is not achievable

            int available = 0;
            for (int i = 0; i < n; i++) {
                boolean done = (mask & (1 << i)) != 0;
                if (!done && (prereq[i] & mask) == prereq[i]) available |= 1 << i;
            }

            if (Integer.bitCount(available) <= k) {
                dp[mask | available] = Math.min(dp[mask | available], dp[mask] + 1);
            } else {
                // enumerate every subset of `available`, keep the ones of size exactly k
                for (int sub = available; sub > 0; sub = (sub - 1) & available) {
                    if (Integer.bitCount(sub) == k) {
                        dp[mask | sub] = Math.min(dp[mask | sub], dp[mask] + 1);
                    }
                }
            }
        }
        return dp[full] >= unreachable ? -1 : dp[full];   // unreachable full set means a cycle
    }

    // ------------------------------------------------------------------

    private static List<List<Integer>> pairs(int[]... edges) {
        List<List<Integer>> out = new ArrayList<>();
        for (int[] e : edges) out.add(Arrays.asList(e[0], e[1]));
        return out;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("typical (1 | 2,3 | 4)",
                minimumSemesters(4, pairs(new int[]{1, 2}, new int[]{1, 3},
                        new int[]{2, 4}, new int[]{3, 4})), 3);

        print("cycle                ",
                minimumSemesters(2, pairs(new int[]{1, 2}, new int[]{2, 1})), -1);

        print("no prerequisites     ",
                minimumSemesters(3, new ArrayList<>()), 1);

        // k-limited trap: greedy picks {1,2} then {3} then {4}; {1,3} then {2,4} wins.
        List<List<Integer>> trap = pairs(new int[]{1, 4}, new int[]{3, 4});
        print("k=2 greedy (wrong)   ", minSemestersGreedyLimit(4, trap, 2), 3);
        print("k=2 optimal DP       ", minSemestersOptimalLimit(4, trap, 2), 2);
    }
}
