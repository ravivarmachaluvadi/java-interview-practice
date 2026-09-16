import java.util.Arrays;

/**
 * You have an array of positive integers representing the values of coins.
 * <p>
 * You need to determine the smallest amount of change that you cannot
 * <p>
 * create with these coins. For instance, given coins [1, 2, 5],
 * <p>
 * the smallest non-constructible change would be 4.
 */
class NonConstructibleChange {
    public static int nonConstructibleChange(int[] coins) {
        Arrays.sort(coins);  // Step 1: Sort the coins
        int currentChange = 0;  // Step 2: Initialize the current change we can construct

        // Step 3: Iterate over each coin
        for (int coin : coins) {
            if (coin > currentChange + 1) {
                // Step 4: If the coin value is more than the smallest non-constructible change
                return currentChange + 1;
            }
            currentChange += coin;  // Step 5: Update the current change
        }
        // Step 6: If all values are constructible, return currentChange + 1
        return currentChange + 1;
    }

    public static void main(String[] args) {
        int[] coins1 = {1, 2, 5};
        int[] coins2 = {1, 1, 1, 1, 1};
        int[] coins3 = {1, 1, 3, 4};
        int[] coins4 = {1, 2, 2, 5};

        System.out.println(nonConstructibleChange(coins1)); // Output: 4
        System.out.println(nonConstructibleChange(coins2)); // Output: 6
        System.out.println(nonConstructibleChange(coins3)); // Output: 11
        System.out.println(nonConstructibleChange(coins4)); // Output: 10
    }
}

