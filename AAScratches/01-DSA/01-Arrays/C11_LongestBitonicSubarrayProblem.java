/*
 * =====================================================================
 *  Longest Bitonic Subarray               GFG / Techie Delight (not LC) | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Find the longest contiguous subarray that is strictly increasing and then strictly
 *   decreasing. A purely increasing or purely decreasing run counts as (degenerate)
 *   bitonic. Return the index range [start, end]; the length is end - start + 1.
 *   Ties keep the earliest range. Equal neighbours break a run.
 *
 * EXAMPLE
 *   [3, 5, 8, 4, 5, 9, 10, 8, 5, 3, 4]  ->  [3, 9]  length 7   [4, 5, 9, 10, 8, 5, 3]
 *   [1, 5, 3, 3, 2]                     ->  [0, 2]  length 3   [1, 5, 3]; plateau at 3,3
 *   [1, 2, 3, 5, 5, 5, 4, 3, 2]         ->  [0, 3]  length 4   tie with [5, 4, 3, 2]
 *   [1, 3, 2, 4, 3, 2, 1, 5]            ->  [2, 6]  length 5   [2, 4, 3, 2, 1]
 *   [4, 4, 4]                           ->  [0, 0]  length 1
 *   []                                  ->  []
 *
 * APPROACH  (up-then-down run scanning)
 *   1. From position i, advance while A[i] < A[i+1] (increasing phase).
 *   2. Then advance while A[i] > A[i+1] (decreasing phase). i now sits on the valley.
 *   3. Record the run's end index NOW; if the length beats the best, keep it.
 *   4. Skip any plateau (A[i] == A[i+1]) and start the next run from where i stopped.
 *      The valley is shared: it ends this run and starts the next one.
 *
 * KEY INSIGHT
 *   The state being tracked is a SHAPE (up, then down), not a value. Two traps:
 *   (a) capture the end index before skipping the plateau, otherwise the reported range
 *       includes equal elements and is not bitonic; (b) starting a new run inside a
 *       plateau is pointless, so skipping it is safe and keeps the scan O(n).
 *   Pattern: whenever a run has phases, model each phase as its own inner while loop and
 *   let the outer loop restart from the shared boundary element.
 *
 * COMPLEXITY
 *   Time  O(n)  i only moves forward; the inner loops share the same index
 *   Space O(1)  a few ints
 *
 * INTERVIEW FOLLOW-UPS
 *   - Longest Mountain (LeetCode 845, C12): same shape but both phases must be non-empty
 *     and length >= 3; solved from the peak outward instead of from the left.
 *   - Longest bitonic SUBSEQUENCE (non-contiguous): O(n^2) LIS from both sides.
 *   - Allow non-strict (plateaus inside a run): change < and > to <= and >=.
 *
 * RUN
 *   main() runs 6 cases (typical, plateau, tie, restart at valley, all equal, empty)
 *   and prints actual vs expected.
 *
 * Fixed: the method computed the answer and discarded it (no return, no print), so
 * main printed nothing; it now returns {start, end}.
 */

import java.util.Arrays;

class LongestBitonicSubarrayProblem {

    /**
     * Returns {start, end} of the longest bitonic subarray (earliest on ties),
     * or an empty array when A is empty.
     */
    public static int[] findBitonicSubarray(int[] A) {
        int n = A.length;
        if (n == 0) return new int[0];

        int bestEnd = 0;
        int bestLen = 1;
        int i = 0;

        while (i + 1 < n) {
            int len = 1;

            // Phase 1: strictly increasing
            while (i + 1 < n && A[i] < A[i + 1]) {
                i++;
                len++;
            }

            // Phase 2: strictly decreasing
            while (i + 1 < n && A[i] > A[i + 1]) {
                i++;
                len++;
            }

            // Record the end BEFORE skipping a plateau so the range stays bitonic.
            if (len > bestLen) {
                bestLen = len;
                bestEnd = i;
            }

            // Skip equal neighbours: a run started inside a plateau has length 1.
            while (i + 1 < n && A[i] == A[i + 1]) {
                i++;
            }
        }

        return new int[]{bestEnd - bestLen + 1, bestEnd};
    }

    private static String describe(int[] range) {
        if (range.length == 0) return "[]";
        return Arrays.toString(range) + " length " + (range[1] - range[0] + 1);
    }

    private static void run(String label, int[] A, String expected) {
        System.out.println(label + ": " + describe(findBitonicSubarray(A))
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical         ", new int[]{3, 5, 8, 4, 5, 9, 10, 8, 5, 3, 4},
                "[3, 9] length 7");
        run("case 2 plateau         ", new int[]{1, 5, 3, 3, 2}, "[0, 2] length 3");
        run("case 3 tie keeps first ", new int[]{1, 2, 3, 5, 5, 5, 4, 3, 2}, "[0, 3] length 4");
        run("case 4 restart at valley", new int[]{1, 3, 2, 4, 3, 2, 1, 5}, "[2, 6] length 5");
        run("case 5 all equal       ", new int[]{4, 4, 4}, "[0, 0] length 1");
        run("case 6 empty           ", new int[]{}, "[]");
    }
}
