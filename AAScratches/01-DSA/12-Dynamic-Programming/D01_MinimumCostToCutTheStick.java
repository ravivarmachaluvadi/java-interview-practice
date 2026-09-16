import java.util.*;

/**
 * Given a wooden stick of length n units. The stick
 * <p>
 * is labelled from 0 to n. Given an integer array
 * <p>
 * cuts where cuts[i] denotes a position you should
 * <p>
 * perform a cut at. Perform the cuts in any order,
 * <p>
 * you can change the order of the cuts as you wish.
 */
// Return the minimum total cost of the cuts
class MinimumCostToCutTheStick {
    // The minimum cost incurred is: 16
    private int func(int i, int j, int[] cuts) {

        if (i > j) return 0;
        int mini = Integer.MAX_VALUE;
        for (int k = i; k <= j; k++) {
            int ans = cuts[j + 1] - cuts[i - 1] + func(i, k - 1, cuts) + func(k + 1, j, cuts);
            mini = Math.min(mini, ans);
        }
        return mini;
    }

    // Function to compute the minimum cost
    public int minCost(int n, List<Integer> cuts) {
        int c = cuts.size();
        /* Convert List<Integer> to int[] */
        int[] newCuts = new int[c + 2];
        newCuts[0] = 0;
        for (int i = 0; i < c; i++) {
            newCuts[i + 1] = cuts.get(i);
        }
        newCuts[c + 1] = n;
        Arrays.sort(newCuts);

        // Call the recursive function to find minimum cost.
        return func(1, c, newCuts);
    }

    public static void main(String[] args) {
        List<Integer> cuts = new ArrayList<>();
        cuts.add(3);
        cuts.add(5);
        cuts.add(1);
        cuts.add(4);
        int n = 7;

        // Create an instance of Solution class
        MinimumCostToCutTheStick sol = new MinimumCostToCutTheStick();

        System.out.println("The minimum cost incurred is: " + sol.minCost(n, cuts));
    }
}
