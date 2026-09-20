/*
 * =====================================================================
 *  Second Smallest Element                                 GfG | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array, return the second smallest DISTINCT value. So
 *   [1, 1, 2] gives 2, not 1: the duplicate 1 does not count twice. If the
 *   array has fewer than 2 elements or fewer than 2 distinct values, there is
 *   no answer and the method throws IllegalArgumentException.
 *
 * EXAMPLE
 *   [4, 2, 2, 1, 1]   ->  2      smallest 1, next distinct value 2
 *   [0, 1]            ->  1 [1, 1, 2]         ->  2      duplicate of the smallest is ignored
 *   [-3, -3, -1, 0]   ->  -1     negatives work the same way
 *   [5, 5, 5]         ->  throws  only one distinct value
 *   [0]               ->  throws  too short
 *
 * APPROACH  (track two extremes, one pass)
 *   1. smallest = second = +infinity (long, so any int is below it).
 *   2. For each num:
 *        if num < smallest: demote smallest to second, then smallest = num
 *        else if smallest < num < second: second = num
 *   3. If second is still +infinity, no second distinct value exists: throw.
 *
 * KEY INSIGHT
 *   Two rules make this work. Order: demote the old smallest BEFORE
 *   overwriting it, or the previous minimum is lost. Distinctness: the
 *   num > smallest guard in the else-branch stops a repeat of the minimum
 *   from being promoted to second place. Using long for the sentinels means
 *   Integer.MIN_VALUE / MAX_VALUE in the input are handled without a flag.
 *
 * COMPLEXITY
 *   Time  O(n)  single pass Space O(1)  two scalars
 *
 * INTERVIEW FOLLOW-UPS
 *   - Second largest? Mirror the comparisons with -infinity sentinels.
 *   - k-th smallest distinct? Sort-and-dedupe O(n log n), or a size-k max-heap.
 *   - Return -1 instead of throwing when there is no answer (GfG style).
 *   - Why not sort and take index 1? O(n log n) and wrong with duplicates.
 *
 * RUN
 *   main() runs 6 cases (typical with duplicates, two elements, duplicate of
 *   the minimum, negatives, all equal, single element) and prints actual vs expected.
 */
import java.util.Arrays;

class SecondSmallest {

    public static int secondSmallest(int[] nums) {
        if (nums.length < 2) {
            throw new IllegalArgumentException("Array must contain at least 2 elements");
        }

        long smallest = Long.MAX_VALUE;        // long so Integer.MAX_VALUE in input still counts
        long secondSmallest = Long.MAX_VALUE;

        for (int num : nums) {
            if (num < smallest) {
                secondSmallest = smallest;     // demote first, then take the new minimum
                smallest = num;
            } else if (num > smallest && num < secondSmallest) {
                secondSmallest = num;          // strictly > smallest keeps duplicates out
            }
        }

        if (secondSmallest == Long.MAX_VALUE) {
            throw new IllegalArgumentException("Array must contain at least 2 distinct values");
        }
        return (int) secondSmallest;
    }

    /** Runs the method and turns an exception into the word "throws" so it can be compared. */
    private static String run(int[] nums) {
        try {
            return String.valueOf(secondSmallest(nums));
        } catch (IllegalArgumentException ex) {
            return "throws";
        }
    }

    private static void check(int[] nums, String expected) {
        System.out.println(Arrays.toString(nums) + " -> " + run(nums) + "   expected " + expected);
    }

    public static void main(String[] args) {
        check(new int[]{4, 2, 2, 1, 1}, "2");        // typical, with duplicates
        check(new int[]{0, 1}, "1");                 // smallest possible valid input
        check(new int[]{1, 1, 2}, "2");              // duplicate of the minimum is ignored
        check(new int[]{-3, -3, -1, 0}, "-1");       // negatives
        check(new int[]{5, 5, 5}, "throws");         // only one distinct value
        check(new int[]{0}, "throws");               // too short
    }
}
