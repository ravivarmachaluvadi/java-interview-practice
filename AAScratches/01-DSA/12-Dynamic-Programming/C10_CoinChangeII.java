/**
 * Function to count the number of ways
 * <p>
 * to make change for a given target sum
 * <p>
 * Input: amount = 5, coins = [1,2,5]
 * <p>
 * Output: 4
 * <p>
 * Explanation: there are four ways to make up the amount:
 * <p>
 * 5=5
 * <p>
 * 5=2+2+1
 * <p>
 * 5=2+1+1+1
 * <p>
 * 5=1+1+1+1+1
 */
// The total number of ways is 4
class CoinChangeII {
    private int MOD = (int) 1e9 + 7;

    private int func(int[] arr, int ind, int T) {
        if (ind == 0)
            return (T % arr[0] == 0) ? 1 : 0;

        int notTaken = func(arr, ind - 1, T);

        int taken = 0;
        if (arr[ind] <= T)
            taken = func(arr, ind, T - arr[ind]);

        return (notTaken + taken) % MOD; // 4
    }

    public int count(int[] coins, int N, int amount) {
        return func(coins, N - 1, amount);
    }
}

class Main {
    public static void main(String[] args) {
        int[] coins = {1, 2, 3};
        int amount = 4;
        int N = coins.length;

        CoinChangeII sol = new CoinChangeII();
        // Print the result
        System.out.println("The total number of ways is " + sol.count(coins, N, amount));
    }
}
