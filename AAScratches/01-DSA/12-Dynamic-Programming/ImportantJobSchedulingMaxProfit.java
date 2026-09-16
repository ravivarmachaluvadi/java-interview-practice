/**
 * Problem: Given arrays of start times, end times and profits for a set of jobs,
 * find the maximum total profit achievable by selecting non‑overlapping jobs.
 *
 * Approach:
 * 1. Wrap each job into an object and sort all jobs by their end time.
 * 2. Use dynamic programming where dp[i] stores the best profit up to job i.
 *    For each job, binary search for the last job that ends before it starts,
 *    add its profit to the current job's profit, and take the maximum with
 *    dp[i‑1].
 *
 * Complexity:
 * Time   O(n log n) – sorting plus n binary searches (log n each).
 * Space  O(n)      – for the jobs array and the dp array.
 */
import java.util.*;
class Job {
    int start, end, profit;

    public Job(int start, int end, int profit) {
        this.start = start;
        this.end = end;
        this.profit = profit;
    }
}

// This is a classic “weighted interval scheduling”
// / “job scheduling with profit” problem.
class ImportantJobSchedulingMaxProfit {

    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        Job[] jobs = new Job[n];

        // Create job array
        for (int i = 0; i < n; i++) {
            jobs[i] = new Job(startTime[i], endTime[i], profit[i]);
        }

        // Sort jobs by end time
        Arrays.sort(jobs, (a, b) -> a.end - b.end);

        // DP array to store the maximum profit until the i-th job
        int[] dp = new int[n];
        dp[0] = jobs[0].profit;

        for (int i = 1; i < n; i++) {
            // Include current job
            int currentProfit = jobs[i].profit;
            /**
             lowerBound represents the index of the last
             non-overlapping job that ends before the current job i starts.
             This is effectively finding the "lower bound"
             */
            int lowerBound = binarySearch(jobs, i);
            if (lowerBound != -1) {
                currentProfit += dp[lowerBound];
            }

            // Exclude current job
            dp[i] = Math.max(dp[i - 1], currentProfit);
        }

        return dp[n - 1];
    }

    // Binary search to find the last job that doesn't conflict
    private int binarySearch(Job[] jobs, int index) {
        int low = 0, high = jobs.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (jobs[mid].end <= jobs[index].start) {
//                above and below same condition
                if (jobs[mid + 1].end <= jobs[index].start) {
                    low = mid + 1;
                } else {
                    return mid;
                }
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] start = {1, 3, 0, 5, 8, 5};
        int[] end   = {2, 4, 6, 7, 9, 9};
        int[] profit= {50, 20, 100, 200, 150, 170};

        ImportantJobSchedulingMaxProfit solver = new ImportantJobSchedulingMaxProfit();
        int maxProfit = solver.jobScheduling(start, end, profit);

        System.out.println("Input:");
        System.out.print("Start times: ");
        for (int s : start) System.out.print(s + " ");
        System.out.println("\nEnd times:   ");
        for (int e : end) System.out.print(e + " ");
        System.out.println("\nProfits:     ");
        for (int p : profit) System.out.print(p + " ");

        System.out.println("\n\nOutput:");
        System.out.println("Maximum Profit = " + maxProfit);
    }
}
