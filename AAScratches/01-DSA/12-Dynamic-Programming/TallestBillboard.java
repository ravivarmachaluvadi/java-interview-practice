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
