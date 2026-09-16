/**
 * Problem: Given an array of rod lengths, partition them into two groups such that the sums of each group are equal.
 * The goal is to maximize this common sum; if no such partition exists, return 0.
 *
 * Approach: Dynamic programming over possible height differences.
 * dp[d] stores the maximum achievable shorter support height when the difference between supports is d.
 * For each rod, update dp by either adding it to the taller side (increase diff), or to the shorter side
 * (reduce diff and increase the shorter height accordingly). A clone of the previous state ensures
 * transitions use only earlier values.
 *
 * Time Complexity: O(n * S) where n is number of rods and S is the total sum of all rod lengths,
 * because each rod iterates over all possible differences up to S.
 * Space Complexity: O(S), storing one array of size (S + 1).
 */
import java.util.Arrays;

class TallestBillboard {

    public static int tallestBillboard(int[] rods) {
        int n = rods.length;
        int sum = 0;
        for (int r : rods) sum += r;
        // dp[j] = max height of the shorter support given difference j
        int[] dp = new int[sum + 1];
        Arrays.fill(dp, -1);
        dp[0] = 0;

        for (int h : rods) {
            int[] prev = dp.clone();
            for (int j = 0; j <= sum; j++) {
                if (prev[j] < 0) continue;
                // 1. skip rod → j unchanged
                // dp[j] = max(dp[j], prev[j]);  // this is implicitly done since dp was cloned

                // 2. add rod to taller support → diff increases by h
                if (j + h <= sum) {
                    dp[j + h] = Math.max(dp[j + h], prev[j]);
                }

                // 3. add rod to shorter support → diff becomes |j - h|
                int newDiff = Math.abs(j - h);
                int gained = prev[j] + Math.min(j, h);
                dp[newDiff] = Math.max(dp[newDiff], gained);
            }
        }

        return dp[0] < 0 ? 0 : dp[0];
    }

    public static void main(String[] args) {
        int[] rods1 = {1, 2, 3, 6};
        System.out.println("Example1: rods = " + Arrays.toString(rods1)
                + " → tallest billboard = " + tallestBillboard(rods1));
        // expected 6

        int[] rods2 = {1, 2, 3, 4, 5, 6};
        System.out.println("Example2: rods = " + Arrays.toString(rods2)
                + " → tallest billboard = " + tallestBillboard(rods2));
        // expected 10

        int[] rods3 = {1, 2};
        System.out.println("Example3: rods = " + Arrays.toString(rods3)
                + " → tallest billboard = " + tallestBillboard(rods3));
        // expected 0
    }
}
