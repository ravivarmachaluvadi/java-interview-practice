/*
 * =====================================================================
 *  Tallest Billboard                              LeetCode 956 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   You have rods of given lengths and want to build a billboard held up by two steel
 *   supports of EQUAL height. Each rod may be welded onto the left support, onto the right
 *   support, or left unused (using every rod is not required).
 *   Return the tallest equal height you can reach, or 0 if the only option is two empty supports.
 *
 * EXAMPLE
 *   rods = [1, 2, 3, 6]     ->  6   (1 + 2 + 3 on one side, 6 on the other)
 *   rods = [1, 2, 3, 4, 5, 6] -> 10 (6 + 4 against 5 + 3 + 2, the rod of length 1 is unused)
 *   rods = [1, 2]           ->  0   (no equal split exists)
 *   rods = []               ->  0   (edge case: nothing to build with)
 *
 * APPROACH  (DP keyed on the DIFFERENCE between the two supports)
 *   1. State: dp[d] = the tallest SHORTER support achievable when the two supports currently
 *      differ by exactly d. The taller support is then dp[d] + d, so one number per difference
 *      describes the whole configuration.
 *   2. Start with dp[0] = 0 (two empty supports) and -1 ("unreachable") everywhere else.
 *   3. Process rods one at a time against a snapshot (prev) of the table, so each rod is used
 *      at most once. For every reachable difference j, there are three moves:
 *        skip       -> difference stays j, height stays prev[j] (already in dp, it was cloned)
 *        taller side-> difference becomes j + h, the shorter support is unchanged: prev[j]
 *        shorter side-> difference becomes |j - h|, and the new shorter support is
 *                       prev[j] + min(j, h)  (whichever side is shorter after the weld)
 *   4. The answer is dp[0]: both supports equal, as tall as possible.
 *
 * KEY INSIGHT
 *   Do not track the two sums; track their DIFFERENCE and the height of the shorter side.
 *   Two configurations with the same difference are interchangeable for the future, so the
 *   state space shrinks from O(S^2) pairs to O(S) differences. Reach for this compression
 *   whenever a problem asks to balance two growing sums.
 *
 * COMPLEXITY
 *   Time  O(n * S)  n rods, each sweeping every difference 0..S where S is the total length
 *   Space O(S)      one table of size S + 1 plus the per-rod clone
 *
 * INTERVIEW FOLLOW-UPS
 *   - Print the actual assignment of rods: store a parent pointer per (rod, difference).
 *   - Partition Equal Subset Sum (LC 416) is the yes/no cousin where every rod must be used.
 *   - Why not meet-in-the-middle? It is O(3^(n/2)) and wins only when S is huge and n <= 20.
 *   - Three supports of equal height: the state becomes a pair of differences, O(n * S^2).
 *
 * RUN
 *   main() runs 4 cases (typical, larger typical, impossible, empty) and prints actual vs expected.
 */
import java.util.Arrays;

class TallestBillboard {

    public static int tallestBillboard(int[] rods) {
        int sum = 0;
        for (int r : rods) sum += r;

        // dp[j] = tallest achievable SHORTER support when supports differ by j; -1 = unreachable
        int[] dp = new int[sum + 1];
        Arrays.fill(dp, -1);
        dp[0] = 0;

        for (int h : rods) {
            // Snapshot so this rod is welded at most once per configuration.
            int[] prev = dp.clone();
            for (int j = 0; j <= sum; j++) {
                if (prev[j] < 0) continue;   // that difference was never reachable

                // Move 1: skip the rod. Already present, because dp starts as a copy of prev.

                // Move 2: weld onto the TALLER support -> gap widens by h, shorter side unchanged.
                if (j + h <= sum) {
                    dp[j + h] = Math.max(dp[j + h], prev[j]);
                }

                // Move 3: weld onto the SHORTER support -> the gap closes to |j - h|.
                // Old heights: shorter = prev[j], taller = prev[j] + j.
                // New heights: prev[j] + h and prev[j] + j, so the new shorter is prev[j]+min(j,h).
                int newDiff = Math.abs(j - h);
                int newShorter = prev[j] + Math.min(j, h);
                dp[newDiff] = Math.max(dp[newDiff], newShorter);
            }
        }

        return dp[0] < 0 ? 0 : dp[0];   // difference 0 means both supports are equal
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] rods1 = {1, 2, 3, 6};
        print("case 1 " + Arrays.toString(rods1), tallestBillboard(rods1), 6);

        int[] rods2 = {1, 2, 3, 4, 5, 6};
        print("case 2 " + Arrays.toString(rods2), tallestBillboard(rods2), 10);

        // Tricky: a non-empty set where no equal split exists at all.
        int[] rods3 = {1, 2};
        print("case 3 " + Arrays.toString(rods3), tallestBillboard(rods3), 0);

        // Edge: no rods, so both supports stay at height 0.
        int[] rods4 = {};
        print("case 4 " + Arrays.toString(rods4), tallestBillboard(rods4), 0);
    }
}
