/*
 * =====================================================================
 *  Josephus Problem                   Classic (LeetCode 1823) | Medium
 * =====================================================================
 *
 * PROBLEM
 *   n people stand in a circle, numbered 1..n. Counting starts at person 1;
 *   every k-th person is removed (k-1 are skipped, the k-th is eliminated) and
 *   counting resumes with the next survivor. Return the 1-based seat of the one
 *   person left alive. Constraints that matter: n >= 1 and k >= 1; k may be
 *   larger than n, and the circle keeps shrinking, so "k-th" wraps around.
 *
 * EXAMPLE
 *   n = 7, k = 3  ->  4   elimination order 3, 6, 2, 7, 5, 1; seat 4 survives
 *   n = 1, k = 5  ->  1   edge case: nobody else to eliminate
 *   n = 5, k = 1  ->  5   k = 1 kills 1, 2, 3, 4 in order; the last seat lives
 *   n = 5, k = 7  ->  4   tricky: k > n, the count wraps the circle twice
 *
 * APPROACH  (recurrence by index remapping)
 *   1. Work entirely in 0-based indices, then add 1 to the final answer.
 *   2. Base case: with n = 1 the only person, index 0, is the survivor.
 *   3. Assume J(n-1) is known: the survivor's index in a circle of n-1 people.
 *   4. In a circle of n, the first person eliminated sits at index (k-1) % n.
 *      What remains is a circle of n-1 people whose counting restarts at the
 *      person that used to be at index k % n.
 *   5. Relabel that restart person as 0. Then index p in the smaller circle is
 *      old index (p + k) % n, so J(n) = (J(n-1) + k) % n.
 *   6. josephusIterative() runs the same recurrence bottom-up in O(1) space,
 *      and josephusBySimulation() removes people from a list as a cross-check.
 *
 * KEY INSIGHT
 *   After one elimination the problem is the SAME problem one size smaller --
 *   only the seat labels moved. Renumbering survivors so the next count starts
 *   at 0 shifts every index by exactly k (mod n), which collapses an O(n*k)
 *   simulation into a one-line recurrence. Recognise this "shrink and relabel"
 *   move whenever a circular process removes one element per round.
 *
 * COMPLEXITY
 *   Time  O(n)  one recurrence step per circle size, from 1 up to n
 *   Space O(n)  recursion depth; the iterative version is O(1) extra space
 *               (simulation is O(n*k) time / O(n) space, shown only to verify)
 *
 * INTERVIEW FOLLOW-UPS
 *   - n up to 1e9: use the iterative loop, or for k = 2 the closed form
 *     2 * (n - largest power of 2 <= n) + 1.
 *   - Print the whole elimination order, not just the survivor (needs the
 *     simulation; the recurrence only tracks the final index).
 *   - Counting starts at seat s instead of seat 1: shift the answer by s - 1.
 *   - Why does deep recursion blow the stack at n ~ 10^5, and how do you fix it?
 *
 * RUN
 *   main() runs 4 cases (typical, n = 1, k = 1, k > n). Each line prints the
 *   recursive, iterative and simulated answers next to the expected value.
 */

import java.util.ArrayList;
import java.util.List;

class JosephusProblem {

    /** Recurrence J(n) = (J(n-1) + k) % n, 0-based index of the survivor. */
    public static int josephus(int n, int k) {
        if (n == 1) {
            return 0; // a circle of one: that person is the survivor
        }
        // Solve the smaller circle, then map its index back into this circle.
        return (josephus(n - 1, k) + k) % n;
    }

    /** Same recurrence bottom-up: no recursion, O(1) extra space. */
    public static int josephusIterative(int n, int k) {
        int survivor = 0; // J(1) = 0
        for (int size = 2; size <= n; size++) {
            survivor = (survivor + k) % size;
        }
        return survivor;
    }

    /**
     * Literal simulation, kept as an independent check on the recurrence.
     * Slower (O(n*k) with list removals) but obviously correct.
     */
    public static int josephusBySimulation(int n, int k) {
        List<Integer> circle = new ArrayList<>();
        for (int seat = 0; seat < n; seat++) {
            circle.add(seat);
        }
        int cursor = 0;
        while (circle.size() > 1) {
            // Step k-1 forward from the current start, wrapping around.
            cursor = (cursor + k - 1) % circle.size();
            circle.remove(cursor);
            // After removal, cursor already points at the next person to count from.
        }
        return circle.get(0);
    }

    /** Convenience wrapper: 1-based seat number, which is what the puzzle asks for. */
    public static int survivingSeat(int n, int k) {
        return josephus(n, k) + 1;
    }

    private static void printCase(String label, int n, int k, int expectedSeat) {
        int recursive = josephus(n, k) + 1;
        int iterative = josephusIterative(n, k) + 1;
        int simulated = josephusBySimulation(n, k) + 1;
        System.out.println(label + " n=" + n + ", k=" + k
                + "  ->  recursive " + recursive
                + ", iterative " + iterative
                + ", simulated " + simulated
                + "   expected " + expectedSeat);
    }

    public static void main(String[] args) {
        printCase("case 1 (typical):  ", 7, 3, 4);
        printCase("case 2 (n = 1):    ", 1, 5, 1);
        printCase("case 3 (k = 1):    ", 5, 1, 5);
        printCase("case 4 (k > n):    ", 5, 7, 4);

        System.out.println("wrapper survivingSeat(7, 3) = " + survivingSeat(7, 3)
                + "   expected 4");
    }
}
