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
