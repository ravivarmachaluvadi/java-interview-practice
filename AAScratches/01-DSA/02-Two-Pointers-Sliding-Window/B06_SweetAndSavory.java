/*
 * =====================================================================
 *  Sweet and Savory (closest pair sum)          AlgoExpert variant | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of sweetness values, an array of savoriness values and a target K, pick one
 *   value from each array so that their sum is as close as possible to K (above or below).
 *   Return the chosen pair as [sweet, savory]. Both arrays are non-empty and unsorted.
 *
 * EXAMPLE
 *   sweetness = [1, 2, 3], savoriness = [3, 4, 5], K = 7   ->  [2, 5]   (2 + 5 = 7, exact)
 *   sweetness = [10],      savoriness = [20],      K = 5   ->  [10, 20] (only one pair exists)
 *   sweetness = [8, 1, 5], savoriness = [9, 2, 4], K = 6   ->  [1, 4]   (1 + 4 = 5, off by 1)
 *
 * APPROACH  (brute force baseline, then sort + converging pointers)
 *   Brute force (findClosestCombination):
 *   1. Try every (sweet, savory) pair, compute the sum.
 *   2. Keep the pair whose |sum - K| is strictly smallest so far.
 *
 *   Two pointers (findClosestCombinationTwoPointers):
 *   1. Sort both arrays.
 *   2. i starts at the smallest sweet, j at the largest savory.
 *   3. Record the pair if |sum - K| beats the best so far.
 *   4. If sum < K, only a bigger sweet can help, so i++; otherwise j-- for a smaller savory.
 *   5. Stop when either pointer runs off its array.
 *
 * KEY INSIGHT
 *   "Closest to target" is pair search plus one running best. The brute force is the safe
 *   O(n*m) answer; the interviewer expects you to replace it with the sorted converging walk,
 *   where each step discards one end because moving the other end could only move the sum
 *   the wrong way. Same skeleton as Two Sum II and 3Sum Closest.
 *
 * COMPLEXITY
 *   Brute force   Time O(n*m)                 Space O(1)
 *   Two pointers  Time O(n log n + m log m)   Space O(n + m) for the two defensive clones
 *                 (O(1) if you are allowed to sort the caller's arrays in place)
 *
 * INTERVIEW FOLLOW-UPS
 *   - AlgoExpert's original: one array, negatives are sweet, positives savory, sum must not
 *     exceed K (split into two arrays first, then the same walk with a "<= K" guard)
 *   - Return all pairs tied for closest, not just one
 *   - Dropping the two clones: this code already sorts copies so the caller's arrays stay
 *     untouched; to avoid the extra arrays entirely, sort one array and binary search it
 *     per element of the other, O(n log m) time and O(1) extra space
 *   - Tie-breaking: both methods keep the first pair found; they can differ on which tie wins
 *
 * RUN
 *   main() runs 3 cases (typical, single element each, unsorted with a tie) through both
 *   methods and prints actual vs expected.
 */
import java.util.Arrays;

class SweetAndSavory {

    /** Baseline: try every pair, keep the one with the smallest |sum - K|. */
    public static int[] findClosestCombination(int[] sweetness, int[] savoriness, int targetK) {
        int bestSweet = 0, bestSavory = 0;
        long bestDiff = Long.MAX_VALUE; // long so the first comparison can never overflow

        for (int sweet : sweetness) {
            for (int savory : savoriness) {
                long diff = Math.abs((long) sweet + savory - targetK);
                if (diff < bestDiff) {
                    bestDiff = diff;
                    bestSweet = sweet;
                    bestSavory = savory;
                }
            }
        }
        return new int[]{bestSweet, bestSavory};
    }

    /** Sort both arrays, then converge from smallest sweet and largest savory. */
    public static int[] findClosestCombinationTwoPointers(int[] sweetness, int[] savoriness,
                                                          int targetK) {
        int[] sweet = sweetness.clone();  // clone so the caller's arrays stay unsorted
        int[] savory = savoriness.clone();
        Arrays.sort(sweet);
        Arrays.sort(savory);

        int bestSweet = 0, bestSavory = 0;
        long bestDiff = Long.MAX_VALUE;
        int i = 0, j = savory.length - 1;

        while (i < sweet.length && j >= 0) {
            int sum = sweet[i] + savory[j];
            long diff = Math.abs((long) sum - targetK);
            if (diff < bestDiff) {
                bestDiff = diff;
                bestSweet = sweet[i];
                bestSavory = savory[j];
            }
            if (sum < targetK) {
                i++; // too small: a larger sweet is the only way up
            } else {
                j--; // too big (or exact): a smaller savory is the only way down
            }
        }
        return new int[]{bestSweet, bestSavory};
    }

    private static void run(String label, int[] sweetness, int[] savoriness, int k,
                            String expected) {
        System.out.println(label + " brute  : "
                + Arrays.toString(findClosestCombination(sweetness, savoriness, k))
                + "   expected " + expected);
        System.out.println(label + " 2-ptr  : "
                + Arrays.toString(findClosestCombinationTwoPointers(sweetness, savoriness, k))
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical ", new int[]{1, 2, 3}, new int[]{3, 4, 5}, 7, "[2, 5]");
        run("case 2 single  ", new int[]{10}, new int[]{20}, 5, "[10, 20]");
        run("case 3 unsorted", new int[]{8, 1, 5}, new int[]{9, 2, 4}, 6, "[1, 4]");
    }
}
