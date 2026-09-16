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
}
