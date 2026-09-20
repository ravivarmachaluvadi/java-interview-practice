/*
 * =====================================================================
 *  Maximum Profit in Job Scheduling        LeetCode 1235 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   You are given three parallel arrays startTime, endTime and profit describing n jobs.
 *   You may run any set of jobs whose time ranges do not overlap; a job that starts exactly
 *   when another ends is allowed. Return the maximum total profit of such a set.
 *
 * EXAMPLE
 *   start=[1,2,3,3] end=[3,4,5,6] profit=[50,10,40,70]  ->  120  (job 1 then job 4)
 *   start=[1,1,1]   end=[2,3,4]   profit=[5,6,4]        ->  6    (all overlap, take the best one)
 *   start=[]        end=[]        profit=[]             ->  0    (nothing to schedule)
 *
 * APPROACH  (weighted interval scheduling: sort by end time + binary search + DP)
 *   1. Pack each (start, end, profit) triple into a Job and sort the jobs by end time.
 *      Sorting by END is what makes the DP work: every job that could precede job i now
 *      sits to its left.
 *   2. dp[i] = best profit using only jobs 0..i (job i may or may not be taken).
 *   3. For job i, "take it" means profit[i] plus dp[p] where p is the RIGHTMOST job with
 *      end <= start of job i. Because ends are sorted, p is found by binary search.
 *   4. "Skip it" is simply dp[i-1]. dp[i] = max(skip, take); the answer is dp[n-1].
 *
 * KEY INSIGHT
 *   The DP only becomes possible after the right ordering. Sorted by end time, the set of
 *   compatible predecessors of a job is a PREFIX of the array, so one binary search collapses
 *   it to a single dp lookup instead of an inner O(n) scan. Recognise this shape whenever
 *   intervals must be chosen without overlap and each carries a weight.
 *
 * COMPLEXITY
 *   Time  O(n log n)  one sort, then n binary searches of O(log n) each
 *   Space O(n)        the Job array plus the dp array (sorting adds O(log n) stack)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Unweighted version (max COUNT of non-overlapping jobs): greedy by earliest end, no DP.
 *   - Reconstruct which jobs were chosen: store the decision at each i and walk back.
 *   - Replace the binary search with a TreeMap of endTime -> bestProfit (floorEntry).
 *   - k machines instead of one: the state grows to (job, machines busy) or becomes min-cost flow.
 *
 * FIXED
 *   The original binarySearch peeked at jobs[mid + 1] without a bounds check and searched the
 *   whole array (including jobs at or after i). Rewritten as a standard "last index with
 *   end <= target" search over the prefix 0..i-1. Also guarded the empty input, which used to
 *   throw on dp[0], and replaced the (a.end - b.end) comparator, which can overflow.
 *
 * RUN
 *   main() runs 4 cases (typical, LeetCode sample, all-overlapping, empty) and prints
 *   actual vs expected.
 */
import java.util.Arrays;

class Job {
    int start, end, profit;

    public Job(int start, int end, int profit) {
        this.start = start;
        this.end = end;
        this.profit = profit;
    }
}

// This is the classic "weighted interval scheduling" problem.
class ImportantJobSchedulingMaxProfit {

    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        if (n == 0) return 0;

        Job[] jobs = new Job[n];
        for (int i = 0; i < n; i++) {
            jobs[i] = new Job(startTime[i], endTime[i], profit[i]);
        }

        // Sorting by END time is the whole trick: compatible predecessors become a prefix.
        Arrays.sort(jobs, (a, b) -> Integer.compare(a.end, b.end));

        // dp[i] = best profit obtainable considering only jobs 0..i
        int[] dp = new int[n];
        dp[0] = jobs[0].profit;

        for (int i = 1; i < n; i++) {
            // Option A: take job i, plus the best we could do before it started.
            int takeProfit = jobs[i].profit;
            int lastCompatible = lastJobEndingBefore(jobs, i);
            if (lastCompatible != -1) {
                takeProfit += dp[lastCompatible];
            }

            // Option B: skip job i and keep whatever we had.
            dp[i] = Math.max(dp[i - 1], takeProfit);
        }

        return dp[n - 1];
    }

    /**
     * Returns the largest index p < index with jobs[p].end <= jobs[index].start,
     * or -1 if no earlier job finishes in time. Ends are sorted, so this is a binary search.
     */
    private int lastJobEndingBefore(Job[] jobs, int index) {
        int target = jobs[index].start;
        int low = 0, high = index - 1;
        int answer = -1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (jobs[mid].end <= target) {
                answer = mid;      // mid works; look right for an even later one
                low = mid + 1;
            } else {
                high = mid - 1;    // mid ends too late, everything right of it does too
            }
        }
        return answer;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        ImportantJobSchedulingMaxProfit solver = new ImportantJobSchedulingMaxProfit();

        // Case 1 (typical): 6 jobs, best set is (1,2,50) + (5,7,200) + (8,9,150).
        int[] start1  = {1, 3, 0, 5, 8, 5};
        int[] end1    = {2, 4, 6, 7, 9, 9};
        int[] profit1 = {50, 20, 100, 200, 150, 170};
        print("case 1 typical      ", solver.jobScheduling(start1, end1, profit1), 420);

        // Case 2 (LeetCode sample): touching intervals are allowed, so 50 + 70 = 120.
        int[] start2  = {1, 2, 3, 3};
        int[] end2    = {3, 4, 5, 6};
        int[] profit2 = {50, 10, 40, 70};
        print("case 2 leetcode #1  ", solver.jobScheduling(start2, end2, profit2), 120);

        // Case 3 (tricky): every job overlaps every other, so only the richest one survives.
        int[] start3  = {1, 1, 1};
        int[] end3    = {2, 3, 4};
        int[] profit3 = {5, 6, 4};
        print("case 3 all overlap  ", solver.jobScheduling(start3, end3, profit3), 6);

        // Case 4 (edge): no jobs at all.
        print("case 4 empty        ", solver.jobScheduling(new int[0], new int[0], new int[0]), 0);
    }
}
