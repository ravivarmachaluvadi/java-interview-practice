/*
 * =====================================================================
 *  P093 DP + Binary Search on Sorted Jobs   Canonical LC 1235 | Hard
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 1235, Maximum Profit in Job Scheduling)
 *   Job i runs from startTime[i] to endTime[i] and pays profit[i]. Choose jobs that do not
 *   overlap (a job may start exactly when another ends) to maximise the total profit.
 *
 * EXAMPLE
 *   start [1,2,3,3], end [3,4,5,6], profit [50,10,40,70]               ->  120
 *   start [1,2,3,4,6], end [3,5,10,6,9], profit [20,20,100,70,60]      ->  150
 *   start [1,1,1], end [2,3,4], profit [5,6,4]                         ->  6
 *
 * RECOGNIZE WHEN
 *   - Intervals that carry a WEIGHT (profit, value, points) and you pick non-overlapping
 *     ones to maximise the total.
 *   - "at most k events", "taxi rides with tips", "attend meetings worth points".
 *   Not this if: every interval is worth the same -> greedy by end time
 *   (P049_SortByEndGreedy) is enough and simpler.
 *
 * TEMPLATE
 *   sort jobs by END time
 *   dp[i] = best profit using the first i jobs (dp[0] = 0)
 *   for i in 1..n:
 *       j = number of jobs (in sorted order) that end <= start of job i   // binary search
 *       dp[i] = max(dp[i - 1],                  // skip job i
 *                   dp[j] + profit[i])          // take it; only compatible jobs before it
 *   answer = dp[n]
 *
 * APPROACH
 *   1. Sort by end time so "everything compatible with job i" is a PREFIX of the order.
 *   2. Binary search the last job that ends no later than job i starts.
 *   3. Take-or-skip recurrence on that prefix.
 *
 * KEY INSIGHT
 *   Greedy fails because a long job can be worth more than many short ones. Sorting by end
 *   turns the compatibility constraint into "a prefix", and a prefix of a sorted array is
 *   found with binary search, so each take-or-skip decision costs O(log n).
 *
 * COMPLEXITY
 *   Time O(n log n), space O(n). With "at most k jobs": O(n k log n) or O(n k).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 1751 Max Events Attended II   at most k events: dp[t][i]; inclusive days, so
 *                                            the previous event must end STRICTLY before
 *           LC 2008 Max Earnings From Taxi   profit = end - start + tip; same recurrence
 *           LC 2054 Two Best Non-Overlapping  k = 2: prefix max + binary search
 *           LC 1353 Max Events (unweighted)  heap greedy -> P046_HeapSchedulingGreedy
 *
 * PITFALLS
 *   - Touching intervals: LC 1235 allows start == previous end; LC 1751 does not (days are
 *     inclusive). The binary search condition is the only difference.
 *   - Sort the three arrays TOGETHER (index array or job objects).
 *   - dp is 1-based (dp[0] = no jobs) to avoid -1 checks after the search.
 *
 * DEEP DIVE
 *   D05_JobSchedulingMaxProfit (12-Dynamic-Programming)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class WeightedJobScheduling {

    // Canonical LC 1235.
    static int jobScheduling(int[] start, int[] end, int[] profit) {
        int n = start.length;
        Integer[] order = new Integer[n];
        for (int i = 0; i < n; i++) {
            order[i] = i;
        }
        Arrays.sort(order, (a, b) -> Integer.compare(end[a], end[b]));
        int[] ends = new int[n];
        for (int i = 0; i < n; i++) {
            ends[i] = end[order[i]];
        }
        int[] dp = new int[n + 1];
        for (int i = 1; i <= n; i++) {
            int job = order[i - 1];
            int j = countEndingAtMost(ends, i - 1, start[job]);
            dp[i] = Math.max(dp[i - 1], dp[j] + profit[job]);
        }
        return dp[n];
    }

    // How many of ends[0..limit) are <= t (ends is sorted).
    private static int countEndingAtMost(int[] ends, int limit, int t) {
        int lo = 0;
        int hi = limit;
        while (lo < hi) {
            int mid = (lo + hi) >>> 1;
            if (ends[mid] <= t) {
                lo = mid + 1;
            } else {
                hi = mid;
            }
        }
        return lo;
    }

    // LC 1751: events[i] = {startDay, endDay, value}, days inclusive; attend at most k.
    static int maxValue(int[][] events, int k) {
        int[][] e = events.clone();
        Arrays.sort(e, (a, b) -> Integer.compare(a[1], b[1]));
        int n = e.length;
        int[] ends = new int[n];
        for (int i = 0; i < n; i++) {
            ends[i] = e[i][1];
        }
        int[][] dp = new int[k + 1][n + 1];        // dp[t][i]: first i events, at most t taken
        for (int i = 1; i <= n; i++) {
            int j = countEndingAtMost(ends, i - 1, e[i - 1][0] - 1);   // must end BEFORE start
            for (int t = 1; t <= k; t++) {
                dp[t][i] = Math.max(dp[t][i - 1], dp[t - 1][j] + e[i - 1][2]);
            }
        }
        return dp[k][n];
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] s1 = {1, 2, 3, 3};
        int[] e1 = {3, 4, 5, 6};
        int[] p1 = {50, 10, 40, 70};
        check("LC 1235 four jobs", jobScheduling(s1, e1, p1), 120);
        check("LC 1235 five jobs",
                jobScheduling(new int[]{1, 2, 3, 4, 6}, new int[]{3, 5, 10, 6, 9},
                new int[]{20, 20, 100, 70, 60}), 150);
        check("LC 1235 all overlap",
                jobScheduling(new int[]{1, 1, 1}, new int[]{2, 3, 4}, new int[]{5, 6, 4}), 6);
        check("LC 1235 touching jobs chain",
                jobScheduling(new int[]{1, 2, 3}, new int[]{2, 3, 4}, new int[]{1, 1, 1}), 3);

        check("LC 1751 k=2", maxValue(new int[][]{{1, 2, 4}, {3, 4, 3}, {2, 3, 1}}, 2), 7);
        check("LC 1751 k=2 one big event",
                maxValue(new int[][]{{1, 2, 4}, {3, 4, 3}, {2, 3, 10}}, 2), 10);
        check("LC 1751 k=3 of four",
                maxValue(new int[][]{{1, 1, 1}, {2, 2, 2}, {3, 3, 3}, {4, 4, 4}}, 3), 9);
        check("LC 1751 shared day conflicts", maxValue(new int[][]{{1, 2, 5}, {2, 3, 5}}, 2), 5);
    }
}
