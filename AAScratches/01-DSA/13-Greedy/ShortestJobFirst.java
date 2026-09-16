/**
 * Problem:
 *   Given an array of job durations, compute the average waiting time when jobs are scheduled
 *   using the Shortest Job First (SJF) strategy.
 *
 * Approach:
 *   1. Sort the job durations in ascending order.
 *   2. Iterate through the sorted list, accumulating total elapsed time and summing each job's
 *      waiting time before it starts.
 *   3. Divide the total waiting time by the number of jobs to obtain the average.
 *
 * Complexity:
 *   Time:  O(n log n) due to sorting (n = number of jobs).
 *   Space: O(1) auxiliary space, ignoring the input array and sort overhead.
 */

import java.util.Arrays;

class ShortestJobFirst {

    static float shortestJobFirst(int[] jobs) {
        Arrays.sort(jobs);
        float waitTime = 0;
        int totalTime = 0;
        int n = jobs.length;

        for (int i = 0; i < n; ++i) {
            waitTime += totalTime;
            totalTime += jobs[i];
        }
        return waitTime / n;
    }

    public static void main(String[] args) {
        int[] jobs = {4, 3, 7, 1, 2};

        System.out.print("Array Representing Job Durations: ");
        for (int i = 0; i < jobs.length; i++) {
            System.out.print(jobs[i] + " ");
        }
        System.out.println();

        float ans = shortestJobFirst(jobs);
        System.out.println("Average waiting time: " + ans); // 4.0
    }
}

