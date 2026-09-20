/*
 * =====================================================================
 *  Squares of a Sorted Array                  LeetCode 977 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array sorted in non-decreasing order (may include negatives),
 *   return an array of the squares of each number, also sorted non-decreasing.
 *   Must run in O(n); sorting the squares (O(n log n)) is the naive answer.
 *
 * EXAMPLE
 *   [-5, -1, 0, 1, 2, 10]  ->  [0, 1, 1, 4, 25, 100]
 *   [-3, -2, -1]           ->  [1, 4, 9]      all negative: squares reverse
 *   [0]                    ->  [0]            single element
 *
 * APPROACH  (Converge inward, fill from the back)
 *   1. Squares are largest at the two ENDS of a sorted array (most negative
 *      or most positive), and smallest somewhere in the middle near zero.
 *   2. Keep left = 0, right = n - 1, and a write index starting at n - 1.
 *   3. Compare the squares at both ends; write the larger one into
 *      result[write], move write left, and move that end inward.
 *   4. Stop when left passes right; result is filled largest to smallest,
 *      which is exactly ascending order.
 *
 * KEY INSIGHT
 *   When you know where the LARGEST elements are (the two ends) but not where
 *   the smallest is, fill the answer backwards from the biggest slot. The
 *   converging pair here produces output rather than swapping in place, and
 *   this "fill from the back" trick reappears in merge-sorted-array in place.
 *
 * COMPLEXITY
 *   Time  O(n)  each step writes one slot and moves one pointer
 *   Space O(n)  the result array; O(1) extra
 *
 * INTERVIEW FOLLOW-UPS
 *   - Alternative: find the split point nearest zero and merge outward
 *     (same O(n), but two loops and a search for the split).
 *   - Why not square then sort? Correct but O(n log n); the sorted input is
 *     the hint that a linear solution exists.
 *   - Overflow: for |value| above ~46340 the square exceeds int; use long.
 *
 * RUN
 *   main() runs 3 cases (typical, all negative, single element) and prints
 *   actual vs expected.
 */
import java.util.Arrays;

class SquaresOfASortedArray {

    public static int[] sortedSquares(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        int left = 0;
        int right = n - 1;
        int write = n - 1; // next slot to fill, from the back

        while (left <= right) {
            int leftSquare = nums[left] * nums[left];
            int rightSquare = nums[right] * nums[right];
            if (leftSquare > rightSquare) {
                result[write--] = leftSquare;
                left++;
            } else {
                result[write--] = rightSquare;
                right--;
            }
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println("case 1 (typical):      "
                + Arrays.toString(sortedSquares(new int[]{-5, -1, 0, 1, 2, 10}))
                + "   expected [0, 1, 1, 4, 25, 100]");
        System.out.println("case 2 (all negative): "
                + Arrays.toString(sortedSquares(new int[]{-3, -2, -1}))
                + "   expected [1, 4, 9]");
        System.out.println("case 3 (single):       "
                + Arrays.toString(sortedSquares(new int[]{0}))
                + "   expected [0]");
    }
}
