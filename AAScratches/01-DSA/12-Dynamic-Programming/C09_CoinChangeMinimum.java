import java.util.*;

/**
 * Function to calculate the minimum number
 * <p>
 * of elements to form the target sum
 */
class CoinChangeMinimum {
    final int mod = (int) 1e9 + 7;

    private int func(int[] arr, int ind, int T) {
        if (ind == 0) {
            if (T % arr[0] == 0)
                return T / arr[0];
            else
                return (int) 1e9;
        }

        int notTaken = func(arr, ind - 1, T);

        int taken = (int) 1e9;
        if (arr[ind] <= T)
            taken = 1 + func(arr, ind, T - arr[ind]);

        return Math.min(notTaken, taken);
    }

    public int minimumCoins(int[] coins, int amount) {
        int n = coins.length;

        int ans = func(coins, n - 1, amount);

        if (ans >= (int) 1e9) return -1;

        return ans;
    }

    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int amount = 7;

        CoinChangeMinimum sol = new CoinChangeMinimum();

        System.out.println("The total number of ways is " + sol.minimumCoins(coins, amount));
    }
}
