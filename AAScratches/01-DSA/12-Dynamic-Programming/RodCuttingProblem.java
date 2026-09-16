/**
 * Problem: Given a rod of length N and an array price[] where price[i] is the selling price of a piece of length i+1,
 * find the maximum revenue obtainable by cutting the rod into integer-length pieces.
 *
 * Approach: Recursively explore two choices at each step:
 * 1) Skip the current length (move to next index).
 * 2) Cut a piece of the current length if it fits, add its price and continue with remaining length,
 *    allowing repeated use of the same length. Track the best profit found.
 *
 * Time Complexity: O(2^N) in the worst case due to exhaustive recursion over all cut combinations.
 * Space Complexity: O(N) for the recursion stack depth (worst-case linear in rod length).
 */
class RodCuttingProblem {
    int maxProfit = -1;

    public void rodCutting(int[] price, int n) {

        rodCuttingProfit(price, 0, n, 1);// 8
    }

    private void rodCuttingProfit(int[] price, int profit, int n, int index) {
        if (n == 0)
            maxProfit = Math.max(maxProfit, profit);

        if (index > price.length || n < 0)
            return;


        rodCuttingProfit(price, profit, n, index + 1);
        if (index <= n)
            rodCuttingProfit(price, profit + price[index - 1], n - index, index);
    }
}

class Main {
    public static void main(String[] args) {
        int[] price = {2, 4, 6, 8};
        int n = price.length;

        // Create an instance of Solution class
        RodCuttingProblem sol = new RodCuttingProblem();
        sol.rodCutting(price, n);
        // Print the result
        System.out.println("The Maximum value is " + sol.maxProfit);

    }
}
