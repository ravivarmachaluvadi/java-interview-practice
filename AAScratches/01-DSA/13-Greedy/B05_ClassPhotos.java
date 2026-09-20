/*
 * =====================================================================
 *  Class Photos                                   AlgoExpert | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Two equal-sized groups of students wear red and blue shirts. They stand in two rows
 *   of the same size, one behind the other. Every student in the back row must be
 *   strictly taller than the student directly in front of them. Return true if such an
 *   arrangement exists, false otherwise.
 *
 * EXAMPLE
 *   red = [5, 8, 1, 3, 4], blue = [6, 9, 2, 4, 5]  ->  true   blue goes in the back
 *   red = [6, 9, 2, 4, 5], blue = [5, 8, 1, 3, 4]  ->  true   same data, roles swapped
 *   red = [1, 2, 3],       blue = [3, 2, 1]        ->  false  tallest heights tie at 3
 *   red = [2],             blue = [2]              ->  false  single pair, equal height
 *
 * APPROACH  (sort both, fix the roles from one extreme, then compare pairwise)
 *   1. Sort both height arrays ascending (on copies, so the caller's arrays survive).
 *   2. Whichever colour owns the single tallest student MUST be the back row: nobody
 *      else can stand behind that student.
 *   3. Walk both sorted arrays in lockstep. At every index the back-row height must be
 *      strictly greater than the front-row height; one failure means the answer is false.
 *   4. If the tallest heights tie, step 2 picks either row and step 3 fails on that pair,
 *      which is correct - equal heights can never satisfy "strictly taller".
 *
 * KEY INSIGHT
 *   Once both rows are sorted, the i-th shortest must stand in front of the i-th shortest;
 *   any other pairing only puts a taller person in front, so if sorted order fails, every
 *   order fails. The global role decision needs exactly one element - the maximum. Pattern
 *   to recognise: sort both sequences, let one extreme fix a global choice, then verify
 *   the rest with a single lockstep pass.
 *
 * COMPLEXITY
 *   Time  O(n log n)  two sorts dominate the single O(n) comparison pass
 *   Space O(n)        the defensive copies; O(log n) extra if you sort in place
 *
 * INTERVIEW FOLLOW-UPS
 *   - What if the rows may differ in size? Then it is a matching problem, not a photo.
 *   - What if back-row students only need to be at least as tall (>= instead of >)?
 *   - Can you answer without sorting? (Counting sort works: heights are bounded.)
 *   - Generalise to k rows, each strictly taller than the one in front.
 *
 * RUN
 *   main() runs 5 cases: the typical case, the same data with roles swapped, a tie at the
 *   maximum, the single-student edge case, and a check that the input was not mutated.
 *   Each prints actual vs expected.
 */

import java.util.Arrays;

class ClassPhotos {

    public static boolean canTakeClassPhoto(int[] redShirtHeights, int[] blueShirtHeights) {
        // Copy before sorting: the caller should not see its input reordered.
        int[] red = redShirtHeights.clone();
        int[] blue = blueShirtHeights.clone();
        Arrays.sort(red);
        Arrays.sort(blue);

        // The colour holding the single tallest student has to be the back row.
        boolean redInBack = red[red.length - 1] > blue[blue.length - 1];
        int[] back = redInBack ? red : blue;
        int[] front = redInBack ? blue : red;

        for (int i = 0; i < back.length; i++) {
            // Strictly taller: equal heights are not allowed.
            if (back[i] <= front[i]) {
                return false;
            }
        }
        return true;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] red = {5, 8, 1, 3, 4};
        int[] blue = {6, 9, 2, 4, 5};
        print("case 1 typical", canTakeClassPhoto(red, blue), true);

        // Same heights, colours swapped: the answer must not depend on which array is which.
        print("case 2 roles swapped", canTakeClassPhoto(blue, red), true);

        // Tallest heights tie at 3, so neither row can stand behind the other.
        int[] tieRed = {1, 2, 3};
        int[] tieBlue = {3, 2, 1};
        print("case 3 tie at max", canTakeClassPhoto(tieRed, tieBlue), false);

        // Single student per row, equal heights.
        print("case 4 single equal", canTakeClassPhoto(new int[]{2}, new int[]{2}), false);

        // Proof that the inputs were not mutated by the calls above.
        print("case 5 input intact", Arrays.toString(red), "[5, 8, 1, 3, 4]");
    }
}
