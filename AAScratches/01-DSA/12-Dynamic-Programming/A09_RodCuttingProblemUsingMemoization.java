import java.util.*;

class Solution {
    /* Function to solve the rod 
    cutting problem using memoization*/
    private int func(int ind, int n, int[] price, int[][] dp) {

        if (ind == 0) return price[0] * n;

        if (dp[ind][n] != -1) return dp[ind][n];

        int notTaken = func(ind - 1, n, price, dp);

        int rodLength = ind + 1;

        int taken = Integer.MIN_VALUE;

        if (rodLength <= n)
            taken = price[ind] + func(ind, n - rodLength, price, dp);

        dp[ind][n] = Math.max(notTaken, taken);
        return dp[ind][n];
    }

    /* Function to initialize the DP table
    and start the rod cutting process*/
    public int rodCutting(int[] price, int n) {
        /* Initialize DP table with 
        -1 (indicating uncalculated states)*/
        int[][] dp = new int[n][n + 1];
        for (int[] row : dp) {
            Arrays.fill(row, -1);
        }

        return func(n - 1, n, price, dp);
    }
}

class RodCuttingProblemUsingMemoization {
    public static void main(String[] args) {
        int[] price = {2, 4, 6, 8};
        int n = price.length;

        // Create an instance of Solution class
        Solution sol = new Solution();

        // Print the result
        System.out.println("The Maximum value is " + sol.rodCutting(price, n));
    }
}
