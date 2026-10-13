/*
 * =====================================================================
 *  Kthreads                                 LeetCode ? | ?
 * =====================================================================
 *
 * PROBLEM
 *   ...
 *
 * EXAMPLE
 *   ...
 *
 * APPROACH
 *   ...
 *
 * KEY INSIGHT
 *   ...
 *
 * COMPLEXITY
 *   Time  O(?)
 *   Space O(?)
 *
 * INTERVIEW FOLLOW-UPS
 *   ...
 *
 * RUN
 *   main() runs the cases below and prints actual vs expected.
 */
class Kthreads {

    static int solve(int[] nums) {
        return 0;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("case 1 typical", solve(new int[]{1, 2, 3}), 6);
        check("case 2 empty  ", solve(new int[]{}), 0);
    }
}
