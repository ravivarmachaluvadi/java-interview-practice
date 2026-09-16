/**
 * You are climbing a staircase. It takes n steps to reach the top.
 * <p>
 * Each time you can either climb 1 or 2 steps.
 * <p>
 * In how many distinct ways can you climb to the top?
 * <p>
 * Example 2:
 * <p>
 * Input: n = 3
 * Output: 3
 * Explanation: There are three ways to climb to the top.
 * 1. 1 step + 1 step + 1 step
 * 2. 1 step + 2 steps
 * 3. 2 steps + 1 step
 */
class ClimbingStairs {

    //Number of ways to climb 5 stairs: 8
    public int climbStairs(int n) {
        if (n == 1) return 1;

        // Initialize base cases
        int first = 1;
        int second = 2;
        // Iterate from step 3 to n
        for (int i = 3; i <= n; i++) {
            // f(n) = f(n-1) + f(n-2)
            int third = first + second;
            first = second;
            second = third;
        }

        // Return the total ways to reach step n
        return second;
    }

    public static void main(String[] args) {
        ClimbingStairs solution = new ClimbingStairs();
        int n = 5; // Example input
        System.out.println("Number of ways to climb " + n + " stairs: " + solution.climbStairs(n));
    }
}
