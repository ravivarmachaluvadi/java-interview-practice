/*
 * =====================================================================
 *  Job Sequencing With Deadlines                  Classic greedy | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Each job is {id, deadline, profit}. Every job takes exactly one unit of time, only
 *   one job can run per unit, and a job earns its profit only if it FINISHES on or
 *   before its deadline. Time slots are 1, 2, 3, ... Choose a subset and an order that
 *   maximises total profit. Return that profit.
 *
 * EXAMPLE
 *   [[1,4,50], [2,1,10], [3,1,40], [4,1,30]]            ->  90
 *     job 1 (profit 50) takes slot 4, job 3 (profit 40) takes slot 1
 *   [[1,2,100], [2,1,19], [3,2,27], [4,1,25], [5,3,15]] ->  142
 *     100 in slot 2, 27 in slot 1, 15 in slot 3
 *   [[1,1,20], [2,1,15], [3,1,10]]                      ->  20
 *     every job wants slot 1, so only the richest one runs (edge case in main)
 *
 * APPROACH  (greedy by profit, place each job in the LATEST free slot)
 *   1. Sort jobs by profit descending (deadline ascending breaks ties, for determinism).
 *   2. Walk the sorted list. For a job with deadline d, find the largest free slot <= d.
 *   3. If such a slot exists, occupy it and bank the profit. Otherwise drop the job.
 *   Two ways to answer step 2, both implemented and both run from main():
 *     a. greedyWithSlotArray   - scan backwards from d over a boolean[] of slots.
 *     b. greedyWithDisjointSet - a DSU where find(s) returns the largest free slot <= s.
 *
 * KEY INSIGHT
 *   Two independent greedy decisions stacked. First, by an exchange argument, if a
 *   feasible schedule exists at all then taking the highest-profit jobs first is never
 *   worse: swapping a cheaper job out for a dearer one that fits keeps feasibility and
 *   raises profit. Second, among the slots a job could use, the LATEST one is always
 *   right, because early slots are the scarce resource that jobs with tighter deadlines
 *   still to come will need. "Take the most valuable item, and consume the least
 *   contested resource that fits it" is the pattern.
 *
 * COMPLEXITY
 *   Time  O(n log n + n*D) for the slot array (D = max deadline), because a single job
 *         may scan back over D slots. The DSU version is O(n log n + D*a(D)), where the
 *         inverse-Ackermann a() is effectively constant, so the sort dominates.
 *   Space O(D) for the slot array or the DSU parent array, plus O(n) for the sorted copy.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Also return WHICH jobs were chosen and in which slot, not just the profit.
 *   - Jobs take different amounts of time: the greedy breaks; this becomes knapsack-like.
 *   - Deadlines up to 1e9 with few jobs: coordinate-compress the slots, or use a
 *     min-heap - push every job, pop the smallest profit whenever the set is infeasible.
 *   - Prove the exchange argument out loud; interviewers ask for it far more than the code.
 *
 * RUN
 *   main() runs 3 cases (two typical, one all-same-deadline) through BOTH approaches and
 *   prints actual vs expected.
 */

import java.util.Arrays;

class JobSequencing {

    /**
     * Shared step: order jobs by profit descending. When profits tie, earlier deadline first -
     * it does not change the total profit, but makes the chosen set deterministic and
     * matches the ordering most references (and interviewers) expect.
     */
    private static int[][] sortedByProfitDesc(int[][] jobs) {
        // shallow copy: we reorder the rows, never mutate them
        int[][] copy = Arrays.copyOf(jobs, jobs.length);
        Arrays.sort(copy, (a, b) -> a[2] != b[2]
                ? Integer.compare(b[2], a[2])   // profit desc
                : Integer.compare(a[1], b[1])); // deadline asc on tie
        return copy;
    }

    private static int maxDeadline(int[][] jobs) {
        int max = 0;
        for (int[] job : jobs) max = Math.max(max, job[1]);
        return max;
    }

    // ------------------------------------------------- Approach 1: boolean slot array
    /**
     * Why "latest free slot": taking the latest slot keeps the earlier ones free for jobs
     * that have tighter deadlines and are still to come in the sorted order.
     */
    static int greedyWithSlotArray(int[][] jobs) {
        int[][] sorted = sortedByProfitDesc(jobs);
        boolean[] slotTaken = new boolean[maxDeadline(jobs) + 1]; // slots are 1..D
        int totalProfit = 0;

        for (int[] job : sorted) {
            for (int slot = job[1]; slot > 0; slot--) {   // scan backwards from the deadline
                if (!slotTaken[slot]) {
                    slotTaken[slot] = true;
                    totalProfit += job[2];
                    break;                                // a job is placed at most once
                }
            }
        }
        return totalProfit;
    }

    // ------------------------------------------------- Approach 2: disjoint set
    /**
     * parent[s] = the largest free slot <= s (0 means "none left").
     * Placing a job in slot s unions s with s-1, so the next lookup for s skips straight to s-1
     * (or further, via path compression) without rescanning taken slots.
     */
    static int greedyWithDisjointSet(int[][] jobs) {
        int[][] sorted = sortedByProfitDesc(jobs);
        int d = maxDeadline(jobs);
        int[] parent = new int[d + 1];
        for (int i = 0; i <= d; i++) parent[i] = i;       // every slot initially free

        int totalProfit = 0;
        for (int[] job : sorted) {
            int freeSlot = find(parent, job[1]);
            if (freeSlot > 0) {                           // 0 = no free slot on or before deadline
                parent[freeSlot] = freeSlot - 1;          // take it: point it at the slot before
                totalProfit += job[2];
            }
        }
        return totalProfit;
    }

    private static int find(int[] parent, int s) {
        if (parent[s] != s) parent[s] = find(parent, parent[s]); // path compression
        return parent[s];
    }

    // ------------------------------------------------- main
    public static void main(String[] args) {
        // {id, deadline, profit}
        int[][][] inputs = {
                {{1, 4, 50}, {2, 1, 10}, {3, 1, 40}, {4, 1, 30}},
                {{1, 2, 100}, {2, 1, 19}, {3, 2, 27}, {4, 1, 25}, {5, 3, 15}},
                {{1, 1, 20}, {2, 1, 15}, {3, 1, 10}}   // edge: every job competes for slot 1
        };
        int[] expected = {90, 142, 20};

        for (int i = 0; i < inputs.length; i++) {
            int[][] jobs = inputs[i];
            System.out.println("case " + (i + 1) + " " + Arrays.deepToString(jobs));
            System.out.println("  slot array  : " + greedyWithSlotArray(jobs)
                    + "   expected " + expected[i]);
            System.out.println("  disjoint set: " + greedyWithDisjointSet(jobs)
                    + "   expected " + expected[i]);
        }
    }
}
