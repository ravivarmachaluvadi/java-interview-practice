/*
 * =====================================================================
 *  Rearrange Array Elements by Sign               LeetCode 2149 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array, reorder it so the signs alternate starting with a non-negative
 *   value, while keeping the relative order of the positives among themselves and of the
 *   negatives among themselves. LeetCode guarantees equal counts of each sign. The general
 *   variant (counts differ) appends whatever is left over, still in original order.
 *
 * EXAMPLE
 *   nums = [3, 1, -2, -5, 2, -4]  ->  [3, -2, 1, -5, 2, -4]
 *   nums = [-1, 1]                ->  [1, -1]                (result must start positive)
 *   nums = [1, 2, 3, -4, -1, 4]   ->  [1, -4, 2, -1, 3, 4]   (general: extra positive at end)
 *   nums = [-3, -2, -1]           ->  [-3, -2, -1]           (general: nothing to interleave)
 *
 * APPROACH  (two write pointers into a fresh output array)
 *   1. Allocate result[n]. posIndex starts at 0, negIndex starts at 1.
 *   2. Scan nums once. A non-negative value goes to result[posIndex] and posIndex += 2;
 *      a negative value goes to result[negIndex] and negIndex += 2.
 *   3. Each pointer only ever moves by 2, so even slots hold positives, odd slots hold
 *      negatives, and each group keeps its original order for free.
 *   General version (rearrange): bucket into two lists, interleave while both have items,
 *   then drain whichever list still has leftovers.
 *
 * KEY INSIGHT
 *   Two independent write cursors let one read pass split the input into two interleaved
 *   streams with no swapping at all. Pattern to recognise: "one cursor per bucket, each on
 *   its own schedule". It is the warm-up for in-place partitions such as the Dutch
 *   national flag partition in C08_Sort012.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass (general version: one bucketing pass plus one write pass)
 *   Space O(n)  the output array (general version: two temporary lists)
 *
 * INTERVIEW FOLLOW-UPS
 *   - O(1) extra space? Possible, but keeping relative order then costs O(n^2) (rotate each
 *     misplaced element into position). Without the order constraint a two-pointer swap
 *     partition works in O(n).
 *   - Counts differ? See rearrange(): leftovers keep their order and go at the end.
 *   - Is 0 positive or negative? Treat 0 as non-negative (LeetCode inputs contain no zeros).
 *
 * RUN
 *   main() runs 5 cases (typical, smallest pair, unequal, all negative, guard) and prints
 *   actual vs expected.
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class AlternatePositiveNegative {

    /** LeetCode version: requires an equal number of non-negative and negative values. */
    public static int[] rearrangeArray(int[] nums) {
        int n = nums.length;
        long positives = Arrays.stream(nums).filter(x -> x >= 0).count();
        if (positives != n - positives) {
            throw new IllegalArgumentException(
                    "rearrangeArray requires an equal count of positive and negative numbers");
        }

        int[] result = new int[n];
        int posIndex = 0;   // next even slot
        int negIndex = 1;   // next odd slot
        for (int num : nums) {
            if (num >= 0) {
                result[posIndex] = num;
                posIndex += 2;
            } else {
                result[negIndex] = num;
                negIndex += 2;
            }
        }
        return result;
    }

    /** General version: counts may differ; leftovers are appended in their original order. */
    public static void rearrange(int[] arr) {
        List<Integer> positives = new ArrayList<>();
        List<Integer> negatives = new ArrayList<>();
        for (int num : arr) {
            if (num >= 0) {
                positives.add(num);
            } else {
                negatives.add(num);
            }
        }

        int pos = 0, neg = 0, write = 0;
        // interleave: even write slots take a positive, odd slots take a negative
        while (pos < positives.size() && neg < negatives.size()) {
            arr[write] = (write % 2 == 0) ? positives.get(pos++) : negatives.get(neg++);
            write++;
        }
        // at most one of these two loops does any work
        while (pos < positives.size()) {
            arr[write++] = positives.get(pos++);
        }
        while (neg < negatives.size()) {
            arr[write++] = negatives.get(neg++);
        }
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        int[] equal = rearrangeArray(new int[]{3, 1, -2, -5, 2, -4});
        print("case 1 equal counts   ", Arrays.toString(equal), "[3, -2, 1, -5, 2, -4]");

        int[] pair = rearrangeArray(new int[]{-1, 1});
        print("case 2 smallest pair  ", Arrays.toString(pair), "[1, -1]");

        int[] unequal = {1, 2, 3, -4, -1, 4};
        rearrange(unequal);
        print("case 3 unequal counts ", Arrays.toString(unequal), "[1, -4, 2, -1, 3, 4]");

        int[] allNegative = {-3, -2, -1};
        rearrange(allNegative);
        print("case 4 all negative   ", Arrays.toString(allNegative), "[-3, -2, -1]");

        String outcome;
        try {
            rearrangeArray(new int[]{1, 2, -3});
            outcome = "no exception";
        } catch (IllegalArgumentException e) {
            outcome = "IllegalArgumentException";
        }
        print("case 5 guard on unequal", outcome, "IllegalArgumentException");
    }
}
