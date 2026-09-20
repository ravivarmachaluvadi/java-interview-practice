/*
 * =====================================================================
 *  Three Largest Numbers                              AlgoExpert | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array with at least 3 elements, return its three largest
 *   values as [first, second, third] in descending order, in one pass and
 *   without sorting. Duplicates are allowed: [9, 9, 6] is a valid answer if
 *   9 appears twice in the input.
 *
 * EXAMPLE
 *   [3, 1, 4, 1, 5, 9, 2, 6, 5]  ->  [9, 6, 5]
 *   [-1, -2, -3]                 ->  [-1, -2, -3]   all negative, still fine
 *   [7, 7, 7, 7]                 ->  [7, 7, 7]      duplicates fill the slots
 *   [5, 9, 9, 6]                 ->  [9, 9, 6]      repeated maximum kept twice
 *   [1, 2]                       ->  [2, 1, MIN]    fewer than 3 elements: sentinel leaks
 *
 * APPROACH  (track three extremes, cascading)
 *   1. first = second = third = Integer.MIN_VALUE.
 *   2. For each num, find the highest slot it beats and shift everything
 *      below that slot down by one:
 *        num > first  : third = second, second = first, first = num
 *        num > second : third = second, second = num
 *        num > third  : third = num
 *   3. Return [first, second, third].
 *
 * KEY INSIGHT
 *   Insert num into a tiny sorted list of size 3, shifting the displaced
 *   values down. The cascade must run from the bottom up (third takes
 *   second's old value BEFORE second takes first's) or a value is lost.
 *   Once you can write this for k = 3 you can write it for any small fixed k;
 *   for large k switch to a size-k min-heap.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass, at most 3 comparisons per element
 *   Space O(1)  three scalars plus the output array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Three largest DISTINCT values? Add num != first / num != second guards.
 *   - k largest for large k? Min-heap of size k, O(n log k); or quickselect, O(n) average.
 *   - Input may contain Integer.MIN_VALUE or have fewer than 3 elements?
 *     Use long sentinels or a count of filled slots instead of MIN_VALUE.
 *   - Three smallest? Mirror with MAX_VALUE sentinels and flipped comparisons.
 *
 * RUN
 *   main() runs 5 cases (typical, all negative, all equal, repeated max, too short)
 *   and prints actual vs expected.
 */
import java.util.Arrays;

class ThreeLargestNumbers {

    public static int[] findThreeLargest(int[] nums) {
        int first = Integer.MIN_VALUE;
        int second = Integer.MIN_VALUE;
        int third = Integer.MIN_VALUE;

        for (int num : nums) {
            if (num > first) {
                // new champion: everyone below moves down one place
                third = second;
                second = first;
                first = num;
            } else if (num > second) {
                third = second;
                second = num;
            } else if (num > third) {
                third = num;
            }
        }
        return new int[]{first, second, third};
    }

    private static void check(int[] nums, int[] expected) {
        System.out.println(Arrays.toString(nums) + " -> "
                + Arrays.toString(findThreeLargest(nums))
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        check(new int[]{3, 1, 4, 1, 5, 9, 2, 6, 5}, new int[]{9, 6, 5});   // typical
        check(new int[]{-1, -2, -3}, new int[]{-1, -2, -3});               // all negative
        check(new int[]{7, 7, 7, 7}, new int[]{7, 7, 7});                  // all equal
        check(new int[]{5, 9, 9, 6}, new int[]{9, 9, 6});                  // repeated maximum
        // Documented limitation: with fewer than 3 elements the MIN_VALUE sentinel shows through.
        check(new int[]{1, 2}, new int[]{2, 1, Integer.MIN_VALUE});
    }
}
