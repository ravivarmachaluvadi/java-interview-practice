/*
 * =====================================================================
 *  Subarray Product Less Than K                       LeetCode 713 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of positive integers nums and an integer k, count the contiguous
 *   subarrays whose product is strictly less than k. All nums[i] >= 1, which is what makes
 *   the product monotonic as the window grows.
 *
 * EXAMPLE
 *   nums = [10, 5, 2, 6], k = 100  ->  8
 *     [10] [5] [2] [6] [10,5] [5,2] [2,6] [5,2,6]   ([10,5,2] = 100 is not < 100)
 *   nums = [1, 2, 3], k = 0        ->  0   nothing is < 0
 *   nums = [1, 1, 1], k = 2        ->  6   every subarray has product 1
 *   nums = [7], k = 7              ->  0   strict inequality
 *   nums = [], k = 5               ->  0   empty input
 *
 * APPROACH  (count subarrays ending at right)
 *   1. If k <= 1 return 0: no product of numbers >= 1 can be below 1.
 *   2. Maintain the product of the window [left, right] as right grows.
 *   3. While the product >= k, divide out nums[left] and move left forward.
 *   4. Now every subarray that ENDS at right and starts anywhere in [left, right] is valid:
 *      there are right - left + 1 of them. Add that to the count.
 *
 * KEY INSIGHT
 *   The window measures a COUNT, not a length. Because the product only shrinks when the
 *   window shrinks (all values >= 1), once [left, right] is valid every shorter suffix of it
 *   is valid too, so "right - left + 1" counts all of them at once with no inner loop.
 *   The same "count subarrays ending here" step is the engine behind the
 *   exactly-K = atMost(K) - atMost(K-1) family.
 *
 * COMPLEXITY
 *   Time  O(n)  left and right each move at most n steps
 *   Space O(1)  a long for the product and two indices
 *
 * INTERVIEW FOLLOW-UPS
 *   - What breaks if nums can contain 0 or negatives? Monotonicity; need a different method.
 *   - Product <= k instead of < k: change one comparison.
 *   - Count subarrays with sum < k (positives): identical structure with + and -.
 *   - Why long for the product? The window can hold up to 1000 in the largest valid state.
 *
 * RUN
 *   main() runs 5 cases (typical, edge, tricky) and prints actual vs expected.
 */
class SubarrayProductLessThanK {

    public static int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1) return 0; // product of numbers >= 1 is at least 1, never < k
        int count = 0;
        int left = 0;
        long product = 1;

        for (int right = 0; right < nums.length; right++) {
            product *= nums[right];

            // shrink from the left while the product is too large
            while (product >= k && left <= right) {
                product /= nums[left];
                left++;
            }

            // every subarray ending at right with start in [left, right] is valid
            count += right - left + 1;
        }
        return count;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 [10,5,2,6] k=100",
                numSubarrayProductLessThanK(new int[]{10, 5, 2, 6}, 100), 8);
        print("case 2 [1,2,3] k=0", numSubarrayProductLessThanK(new int[]{1, 2, 3}, 0), 0);
        print("case 3 [1,1,1] k=2", numSubarrayProductLessThanK(new int[]{1, 1, 1}, 2), 6);
        print("case 4 [7] k=7", numSubarrayProductLessThanK(new int[]{7}, 7), 0);
        print("case 5 empty k=5", numSubarrayProductLessThanK(new int[]{}, 5), 0);
    }
}
