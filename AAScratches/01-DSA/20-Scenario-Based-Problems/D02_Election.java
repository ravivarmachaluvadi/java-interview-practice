/*
 * =====================================================================
 *  Election / Josephus Problem                          LC 1823 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   n students numbered 1..n sit in a circle. The teacher starts at student 1 and sings
 *   a song of length k, walking past k students; the student she stops on is removed.
 *   She resumes from the student after the removed one and repeats until one is left.
 *   Return the number of the surviving student. n >= 1, k >= 1.
 *
 * EXAMPLE
 *   n = 4, k = 2  ->  1
 *     circle 1,2,3,4. She walks past 1 and 2, so 2 goes. Then 3 and 4, so 4 goes.
 *     Then 1 and 3, so 3 goes. Student 1 survives.
 *   n = 1, k = 1  ->  1     (edge: the only student wins by default)
 *   n = 5, k = 3  ->  4
 *
 * APPROACH  (Josephus recurrence, in 1-based numbering)
 *   1. Solve the 0-indexed problem first. Let g(n) be the survivor's SEAT INDEX
 *      (0..n-1) when the count always restarts from seat 0. Then g(1) = 0 and
 *      g(n) = (g(n-1) + k) % n.
 *   2. Why: after the first removal, n-1 students remain and the counting restarts from
 *      the seat just after the one removed. That smaller circle is the same problem,
 *      only rotated by k seats. So take the answer for n-1 and rotate it back by k.
 *   3. Convert to 1-based numbering. Substituting f(n) = g(n) + 1 gives the form used
 *      in the code:  f(1) = 1,  f(n) = (f(n-1) + k - 1) % n + 1.
 *      The "- 1" undoes the 1-based offset before the modulo; the "+ 1" restores it.
 *   4. whoIsElected uses that recurrence directly. whoIsElectedIterative runs the same
 *      recurrence bottom-up from n = 1 upward, which removes the call stack.
 *
 * KEY INSIGHT
 *   Do not simulate the circle - collapse it. Removing one student turns the problem of
 *   size n into the SAME problem of size n-1 whose seats have been rotated by k, so the
 *   whole thing is one line of arithmetic. The only place people lose this in an
 *   interview is the 1-based indexing: derive it 0-based, then add 1 at the very end.
 *   Pattern to recognise: "people in a circle, every k-th one is eliminated" is Josephus.
 *
 * COMPLEXITY
 *   whoIsElected (recursive)   Time O(n)   one call per size.  Space O(n) call stack -
 *                              this is the version that dies with StackOverflowError
 *                              around n in the tens of thousands.
 *   whoIsElectedIterative      Time O(n)   one loop pass.      Space O(1).
 *   whoIsElectedBySimulation   Time O(n^2) ArrayList.remove shifts elements each time.
 *                              Space O(n). Only here to prove the formula is right.
 *
 * INTERVIEW FOLLOW-UPS
 *   - n up to 10^9 with small k: the O(n) loop is too slow. There is an O(k log n)
 *     trick that skips whole blocks of eliminations at a time.
 *   - k = 2 has a closed form: write n = 2^m + L, the survivor is 2L + 1.
 *   - Return the elimination ORDER, not just the survivor: that needs a simulation,
 *     and an order-statistic BIT or a balanced BST gets it to O(n log n).
 *   - Variant: the teacher alternates direction, or k changes each round. The recurrence
 *     still works as long as each round removes exactly one and restarts after it.
 *
 * RUN
 *   main() runs 8 cases through all three implementations: the doc example, the n = 1
 *   edge case, k = 1 (plain round robin), and larger n up to 1000. Each prints actual
 *   vs expected; the simulation is skipped above n = 2000 because it is quadratic.
 */

import java.util.*;

class Election {

    /** Josephus recurrence, top-down. O(n) stack depth. */
    public static int whoIsElected(int n, int k) {
        if (n == 1) return 1;
        return (whoIsElected(n - 1, k) + k - 1) % n + 1;
    }

    /** Same recurrence built bottom-up, so there is no call stack to overflow. */
    public static int whoIsElectedIterative(int n, int k) {
        int survivor = 1;                       // f(1) = 1
        for (int size = 2; size <= n; size++) {
            survivor = (survivor + k - 1) % size + 1;
        }
        return survivor;
    }

    /**
     * Brute-force simulation of the actual circle. Not the interview answer - it exists
     * so you can verify the recurrence and, if asked, read off the elimination order.
     */
    public static int whoIsElectedBySimulation(int n, int k) {
        List<Integer> circle = new ArrayList<>(n);
        for (int student = 1; student <= n; student++) {
            circle.add(student);
        }

        int seat = 0;                           // the teacher starts standing at student 1
        while (circle.size() > 1) {
            seat = (seat + k - 1) % circle.size();   // walk past k students, wrapping round
            circle.remove(seat);
            // After the removal this index already points at the next student; wrap if
            // we just removed the last seat in the list.
            if (seat == circle.size()) seat = 0;
        }
        return circle.get(0);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        // { n, k, expected survivor }
        int[][] cases = {
                {1, 1, 1},          // edge: a single student
                {2, 2, 1},
                {4, 2, 1},          // the worked example in the header
                {5, 3, 4},
                {6, 4, 5},
                {7, 1, 7},          // k = 1: everyone is removed in order, the last seat wins
                {100, 2, 73},
                {1000, 5, 763}      // still inside the n <= 2000 simulation guard
        };

        for (int[] c : cases) {
            int n = c[0], k = c[1], expected = c[2];
            String label = "n=" + n + " k=" + k;

            print(label + " recursive", whoIsElected(n, k), expected);
            print(label + " iterative", whoIsElectedIterative(n, k), expected);
            if (n <= 2000) {
                print(label + " simulated", whoIsElectedBySimulation(n, k), expected);
            }
            System.out.println();
        }
    }
}
