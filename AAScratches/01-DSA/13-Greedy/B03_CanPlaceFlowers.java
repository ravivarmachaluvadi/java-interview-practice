/*
 * =====================================================================
 *  Can Place Flowers                                 LeetCode 605 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   flowerbed is an array of 0s (empty plot) and 1s (already planted). No two flowers may
 *   ever occupy adjacent plots, and the given bed already respects that rule. Return true
 *   if n MORE flowers can be planted without breaking the rule.
 *
 * EXAMPLE
 *   [1, 0, 0, 0, 1], n = 1  ->  true    plant at index 2
 *   [1, 0, 0, 0, 1], n = 2  ->  false   index 2 is the only legal plot
 *   [0],             n = 1  ->  true    a lone empty plot has no neighbours (edge case)
 *   [0, 0, 0],       n = 2  ->  true    plant at index 0 and index 2
 *
 * APPROACH  (left-to-right scan, commit the moment a plot is legal)
 *   1. Walk the bed from left to right with a counter of flowers planted.
 *   2. Plot i is plantable when it is 0 AND its left neighbour is 0 or off the end AND its
 *      right neighbour is 0 or off the end. Treat the outside of the array as empty.
 *   3. Plant immediately (set flowerbed[i] = 1) and bump the counter. Writing the 1 back is
 *      what stops i+1 from also being taken.
 *   4. Return counter >= n. An early true is returned as soon as the counter reaches n.
 *   canPlaceFlowersNoMutation() is the same greedy written as a run-length count, for
 *   interviewers who say "do not modify the input". Both run from main().
 *
 * KEY INSIGHT
 *   Planting at the LEFTMOST legal plot is never worse. Skipping it to plant further right
 *   can only block the same plots or more, so the leftmost choice dominates - a standard
 *   exchange argument. The mutation is not a hack, it is how the algorithm remembers the
 *   commitment so the very next index sees an occupied neighbour.
 *   Pattern: scan left to right, take every opportunity the instant it is legal, and record
 *   the take in the state the scan reads.
 *
 * COMPLEXITY
 *   Time  O(n)   a single pass, constant work per plot
 *   Space O(1)   the scanning version writes into the input; the no-mutation version
 *                keeps one run-length counter
 *
 * INTERVIEW FOLLOW-UPS
 *   - "Do not modify the input" - the run-length version below, or restore the array after.
 *   - Return the maximum number of flowers plantable, not just a yes/no for n.
 *   - Generalise to "at least k empty plots between any two flowers": a run of z zeros
 *     then admits (z + k) / (k + 1) - 1 flowers in the interior.
 *   - Why not count the zeros and divide by two? [1,0,1,0,1] has two zeros and fits none.
 *
 * RUN
 *   main() runs 4 cases (typical, infeasible, single plot, all empty) through BOTH methods
 *   and prints actual vs expected.
 */

import java.util.Arrays;

class CanPlaceFlowers {

    /**
     * Author's scan. NOTE: this writes 1s into flowerbed - callers must pass a copy if they
     * still need the original. That write is deliberate: it is how the scan remembers a plant.
     */
    public boolean canPlaceFlowers(int[] flowerbed, int n) {
        int count = 0;
        for (int i = 0; i < flowerbed.length; i++) {
            // off the end of the array counts as an empty neighbour
            boolean leftFree = (i == 0) || flowerbed[i - 1] == 0;
            boolean rightFree = (i == flowerbed.length - 1) || flowerbed[i + 1] == 0;

            if (flowerbed[i] == 0 && leftFree && rightFree) {
                flowerbed[i] = 1;   // commit, so index i+1 now sees an occupied left neighbour
                count++;
                if (count >= n) return true;   // early exit, no need to fill the whole bed
            }
        }
        return count >= n;          // also covers n == 0
    }

    /**
     * Same greedy, no writes to the input. Pad the bed with an imaginary empty plot at each
     * end; a maximal run of z zeros then holds (z - 1) / 2 flowers.
     */
    public boolean canPlaceFlowersNoMutation(int[] flowerbed, int n) {
        int planted = 0;
        int emptyRun = 1;           // the imaginary empty plot before index 0

        for (int plot : flowerbed) {
            if (plot == 0) {
                emptyRun++;
            } else {
                planted += (emptyRun - 1) / 2;   // close off the run at this flower
                emptyRun = 0;                    // a planted plot is not part of any run
            }
        }
        planted += emptyRun / 2;    // trailing run, +1 imaginary plot then (run - 1) / 2
        return planted >= n;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        CanPlaceFlowers solution = new CanPlaceFlowers();

        int[][] beds = {{1, 0, 0, 0, 1}, {1, 0, 0, 0, 1}, {0}, {0, 0, 0}};
        int[] targets = {1, 2, 1, 2};
        boolean[] expected = {true, false, true, true};

        for (int i = 0; i < beds.length; i++) {
            String label = "case " + (i + 1) + " " + Arrays.toString(beds[i])
                    + ", n = " + targets[i];
            // clone: the scanning method plants into the array it is given
            print(label + " [scan]    ",
                    solution.canPlaceFlowers(beds[i].clone(), targets[i]), expected[i]);
            print(label + " [no-mut]  ",
                    solution.canPlaceFlowersNoMutation(beds[i], targets[i]), expected[i]);
        }
    }
}
