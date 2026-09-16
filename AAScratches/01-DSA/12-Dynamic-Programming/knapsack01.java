import java.util.*;

/**
 * The goal is to maximize the sum of the values of
 * <p>
 * the selected items while keeping the total
 * <p>
 * weight within the knapsack's capacity.
 */
class knapsack01 {
    private int func(int[] wt, int[] val, int ind, int W) {
        if (ind < 0 || W == 0)
            return 0;

        int notTaken = func(wt, val, ind - 1, W);
        int taken = 0;

        if (wt[ind] <= W)
            taken = val[ind] + func(wt, val, ind - 1, W - wt[ind]);

        return Math.max(notTaken, taken);
    }

    public int knapsack01(int[] wt, int[] val, int n, int W) {
        return func(wt, val, n - 1, W);
    }

    public static void main(String[] args) {
        int[] wt = {1, 2, 4, 5};
        int[] val = {5, 4, 8, 6};
        int W = 5;
        int n = wt.length;

        // Create an instance of Solution class
        knapsack01 sol = new knapsack01();

        // Call the function to find the maximum value
        int result = sol.knapsack01(wt, val, n, W);

        // Output the result
        System.out.println("The Maximum value of items is " + result);
    }
}
