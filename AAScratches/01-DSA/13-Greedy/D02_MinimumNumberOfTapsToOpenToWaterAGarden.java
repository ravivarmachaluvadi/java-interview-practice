/*
 * =====================================================================
 *  Minimum Number of Taps to Open to Water a Garden   LeetCode 1326 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   A garden is the segment [0, n] on a line. Tap i sits at point i and waters
 *   the closed interval [i - ranges[i], i + ranges[i]] (a range of 0 waters
 *   nothing). Return the minimum number of taps to open so every point of
 *   [0, n] is watered, or -1 if the whole garden cannot be covered.
 *
 * EXAMPLE
 *   n = 5, ranges = [3,4,1,1,0,0]  ->  1   tap 1 alone covers [-3,5] -> [0,5]
 *   n = 3, ranges = [0,0,0,0]      ->  -1  every tap waters nothing
 *   n = 8, ranges = [4,0,0,0,0,0,0,0,4] -> 2  tap 0 covers [0,4], tap 8 [4,8]
 *   n = 1, ranges = [1,0]          ->  1   tap 0 covers [0,1] on its own
 *
 * APPROACH  (interval covering reduced to Jump Game II)
 *   1. Collapse the taps into a jump array. For each tap i compute
 *      left = max(0, i - ranges[i]) and right = min(n, i + ranges[i]), then
 *      maxReachFrom[left] = max(maxReachFrom[left], right). Overlapping taps
 *      that start at the same point keep only the one that reaches furthest -
 *      a shorter one is never useful.
 *   2. Now run Jump Game II verbatim over positions 0..n-1:
 *      farthest = max(farthest, maxReachFrom[i]) is the best right edge of any
 *      tap whose interval has already started by i.
 *   3. If farthest <= i the water stops at i and nothing extends past it, so the
 *      garden is uncoverable: return -1.
 *   4. When i reaches currentEnd, the current tap's coverage is used up: open
 *      one more tap (tapsOpened++) and set currentEnd = farthest.
 *
 * KEY INSIGHT
 *   The reduction is the whole problem. Once every tap is rewritten as "standing
 *   at position left, I can jump to right", covering [0, n] with fewest taps IS
 *   reaching index n in fewest jumps. Keeping only the furthest reach per left
 *   endpoint is safe because two intervals with the same start are comparable -
 *   the shorter is strictly dominated.
 *   Pattern to recognise: minimum intervals to cover a segment -> bucket by left
 *   endpoint, then layered frontier. Video Stitching is the identical problem.
 *   Note we iterate i < n, not i <= n: reaching n ends the job, it is not a
 *   position you need to jump out of.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass to build maxReachFrom, one pass to sweep it
 *   Space O(n)  the maxReachFrom array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Same problem stated as intervals with arbitrary endpoints (Video Stitching).
 *   - The DP alternative: dp[j] = min taps to cover [0, j]; why is it O(n^2)?
 *   - What if taps had costs and you wanted minimum total cost, not count?
 *   - Why is the "furthest reach per left endpoint" pruning lossless?
 *
 * RUN
 *   main() runs 4 cases (typical, impossible, chained taps, smallest garden) and
 *   prints actual vs expected.
 */

import java.util.Arrays;

class MinimumNumberOfTapsToOpenToWaterAGarden {

    /**
     * Minimum taps to water all of [0, n], or -1 when it cannot be covered.
     */
    public int minTaps(int n, int[] ranges) {
        // maxReachFrom[l] = furthest right edge among taps whose interval starts at l.
        int[] maxReachFrom = new int[n + 1];

        for (int i = 0; i <= n; i++) {
            int left = Math.max(0, i - ranges[i]);
            int right = Math.min(n, i + ranges[i]);
            maxReachFrom[left] = Math.max(maxReachFrom[left], right);
        }

        int tapsOpened = 0;
        int currentEnd = 0;   // right edge of the coverage already paid for
        int farthest = 0;     // best right edge reachable from anything seen so far

        // Position n itself needs no tap to jump out of - arriving there is the goal.
        for (int i = 0; i < n; i++) {
            farthest = Math.max(farthest, maxReachFrom[i]);

            // Nothing seen so far waters past i, so point i (or just after) stays dry.
            if (farthest <= i) {
                return -1;
            }
            if (i == currentEnd) {
                tapsOpened++;
                currentEnd = farthest;
            }
        }
        return tapsOpened;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        MinimumNumberOfTapsToOpenToWaterAGarden sol =
                new MinimumNumberOfTapsToOpenToWaterAGarden();

        // typical: one wide tap covers the whole garden
        int[] r1 = {3, 4, 1, 1, 0, 0};
        print("case 1 n=5 " + Arrays.toString(r1), sol.minTaps(5, r1), 1);

        // edge: every tap is dead, nothing can be covered
        int[] r2 = {0, 0, 0, 0};
        print("case 2 n=3 " + Arrays.toString(r2), sol.minTaps(3, r2), -1);

        // tricky: two taps that meet exactly at point 4 - touching is enough
        int[] r3 = {4, 0, 0, 0, 0, 0, 0, 0, 4};
        print("case 3 n=8 " + Arrays.toString(r3), sol.minTaps(8, r3), 2);

        // edge: the smallest legal garden
        int[] r4 = {1, 0};
        print("case 4 n=1 " + Arrays.toString(r4), sol.minTaps(1, r4), 1);
    }
}
