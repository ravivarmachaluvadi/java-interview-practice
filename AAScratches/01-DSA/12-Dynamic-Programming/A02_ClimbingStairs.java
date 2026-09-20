/*
 * =====================================================================
 *  Climbing Stairs                        LeetCode 70 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   A staircase has n steps. From any step you may climb 1 step or 2 steps.
 *   Return how many distinct ordered ways there are to reach the top.
 *   LeetCode guarantees 1 <= n <= 45, so the answer fits in an int.
 *
 * EXAMPLE
 *   n = 3  ->  3     (1+1+1, 1+2, 2+1)
 *   n = 5  ->  8 n = 1  ->  1     (the smallest real case, main() runs it)
 *   n = 2  ->  2     (1+1, 2)
 *
 * APPROACH  (Fibonacci as a rolling O(1) DP)
 *   1. Let ways(i) be the number of ways to stand on step i. The last move onto
 *      step i was either a 1-step from i-1 or a 2-step from i-2, and those two
 *      sets of routes are disjoint, so ways(i) = ways(i-1) + ways(i-2).
 *   2. Seed the recurrence: ways(1) = 1, ways(2) = 2.
 *   3. Walk i from 3 to n keeping only the previous two counts in two variables
 *      (first = ways(i-2), second = ways(i-1)), overwriting them each step.
 *   4. After the loop, second holds ways(n). Return it.
 *
 * KEY INSIGHT
 *   Count by the LAST move, not the first. Fixing the final step splits every
 *   route into two disjoint families whose sizes are already-solved smaller
 *   answers - that is what makes the recurrence add instead of multiply, and it
 *   is the counting argument to say out loud in an interview. Then notice the
 *   recurrence reaches back exactly two positions, so a whole dp[] array is
 *   waste: two rolling variables give the same answer in O(1) space. Every 1D
 *   DP later in this folder reuses that same space trick.
 *
 * COMPLEXITY
 *   Time  O(n)   one pass, constant work per step
 *   Space O(1)   two int variables; no dp table is kept
 *
 * INTERVIEW FOLLOW-UPS
 *   - Steps of 1, 2 or 3 -> three-term recurrence, three rolling variables.
 *   - Arbitrary allowed step sizes -> ways(i) = sum of ways(i - s) over steps s.
 *   - Some steps are broken and cannot be used -> set those ways(i) to 0.
 *   - Min Cost Climbing Stairs (LC 746): same shape, min instead of sum.
 *   - n up to 1e18 mod 1e9+7 -> matrix exponentiation, as in A01_Fibonacci.
 *
 * RUN
 *   main() runs 4 cases (n = 1, 2, 3, 5) and prints actual vs expected.
 */

class ClimbingStairs {

    public int climbStairs(int n) {
        if (n == 0) return 1;      // already at the top: exactly one (empty) way
        if (n <= 2) return n;      // ways(1) = 1, ways(2) = 2

        int first = 1;             // ways(i - 2), starts as ways(1)
        int second = 2;            // ways(i - 1), starts as ways(2)

        for (int i = 3; i <= n; i++) {
            int third = first + second;   // ways(i) = ways(i-1) + ways(i-2)
            first = second;               // slide the window forward one step
            second = third;
        }

        return second;             // now holds ways(n)
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        ClimbingStairs solution = new ClimbingStairs();

        print("case 1 n = 5 (typical) ", solution.climbStairs(5), 8);
        print("case 2 n = 1 (smallest)", solution.climbStairs(1), 1);
        print("case 3 n = 2 (base)    ", solution.climbStairs(2), 2);
        print("case 4 n = 3 (listed)  ", solution.climbStairs(3), 3);
    }
}
