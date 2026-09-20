/*
 * =====================================================================
 *  Shortest Job First: average waiting time        Classic OS problem | Easy
 * =====================================================================
 *
 * PROBLEM
 *   You are given the durations of n jobs that all arrive at time 0 on a single CPU.
 *   You may run them in any order, one at a time, with no preemption. A job's waiting
 *   time is how long it sits in the queue before it starts. Return the average waiting
 *   time of the schedule that minimises it.
 *
 * EXAMPLE
 *   jobs = [4, 3, 7, 1, 2]  ->  4.0
 *     run order 1, 2, 3, 4, 7 gives waits 0, 1, 3, 6, 10  =  20 / 5 = 4.0
 *   jobs = [5]              ->  0.0    single job never waits (edge case in main)
 *   jobs = [5, 5, 5]        ->  5.0    all equal: waits 0, 5, 10 = 15 / 3 = 5.0
 *
 * APPROACH  (exchange-argument greedy: sort ascending)
 *   1. Sort the durations ascending. Shortest job first.
 *   2. Sweep once, carrying totalTime = the clock when the next job starts.
 *   3. Each job waits exactly totalTime, so add that to waitTime, then advance
 *      totalTime by this job's duration.
 *   4. Return waitTime / n.
 *
 * KEY INSIGHT
 *   A job of length L placed at position k delays every one of the (n - k) jobs behind
 *   it by L. So a long job early is "paid for" many times. Swap any adjacent pair where
 *   the longer job runs first: the pair's combined wait strictly drops. No adjacent
 *   swap can improve the fully ascending order, so ascending is optimal.
 *   Pattern to recognise: when each choice taxes everything still queued behind it,
 *   sort by that cost ascending and take the cheapest first.
 *
 * COMPLEXITY
 *   Time  O(n log n)  the sort dominates; the accumulation sweep is O(n)
 *   Space O(n)        we copy the input so the caller's array is left untouched
 *                     (sorting in place would be O(1) but is a surprising side effect)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Jobs arrive at different times: you now need a min-heap of the available jobs
 *     (that is "preemptive SJF" / shortest remaining time first).
 *   - Weighted jobs (job i costs w_i per unit waited): sort by duration / weight
 *     ascending. This is Smith's rule, the same exchange argument with weights.
 *   - Why is SJF not used as-is by real schedulers? Long jobs can starve forever.
 *   - Return the average completion (turnaround) time instead of waiting time.
 *
 * RUN
 *   main() runs 3 cases (typical, single job, all-equal) and prints actual vs expected.
 */

import java.util.Arrays;

class ShortestJobFirst {

    /**
     * Average waiting time under the Shortest-Job-First schedule.
     * Works on a copy so the caller's array keeps its original order.
     */
    static float shortestJobFirst(int[] jobs) {
        if (jobs == null || jobs.length == 0) return 0f;

        int[] sorted = jobs.clone();
        Arrays.sort(sorted);                 // shortest job first

        float waitTime = 0;                  // sum of every job's wait
        int totalTime = 0;                   // clock at which the next job starts

        for (int duration : sorted) {
            waitTime += totalTime;           // this job waited for everything before it
            totalTime += duration;           // and pushes the clock forward by its own length
        }
        return waitTime / sorted.length;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] typical = {4, 3, 7, 1, 2};
        int[] single = {5};
        int[] allEqual = {5, 5, 5};

        print("case 1 " + Arrays.toString(typical), shortestJobFirst(typical), "4.0");
        print("case 2 " + Arrays.toString(single), shortestJobFirst(single), "0.0");
        print("case 3 " + Arrays.toString(allEqual), shortestJobFirst(allEqual), "5.0");

        // the input is never reordered by the call above
        print("case 1 input unchanged", Arrays.toString(typical), "[4, 3, 7, 1, 2]");
    }
}
