/*
 * =====================================================================
 *  P008 Prefix / Suffix Two Passes   Canonical LC 238 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 238, Product of Array Except Self)
 *   Return answer where answer[i] is the product of every nums[j] with j != i, in O(n)
 *   time, without using division.
 *
 * EXAMPLE
 *   [1, 2, 3, 4]       ->  [24, 12, 8, 6]
 *   [-1, 1, 0, -3, 3]  ->  [0, 0, 9, 0, 0]     the zero is why division is a trap
 *
 * RECOGNIZE WHEN
 *   - answer[i] depends on "everything to the left of i" AND "everything to the right".
 *   - Words like "except self", "water above each bar", "nearest X on either side",
 *     "max to the left / max to the right".
 *   Not this if: you need the nearest GREATER element on each side, not an aggregate ->
 *   P037_NextGreaterElement; you need the aggregate of a sliding range ->
 *   P015_FixedWindowAggregate.
 *
 * TEMPLATE
 *   left[0] = identity;  for i in 1..n-1:    left[i]  = op(left[i-1],  a[i-1])
 *   right[n-1] = identity; for i in n-2..0:  right[i] = op(right[i+1], a[i+1])
 *   answer[i] = combine(left[i], right[i])
 *   // O(1) extra: write left into answer, then fold right in with one running variable
 *
 * APPROACH
 *   1. Left pass: answer[i] = product of nums[0..i-1].
 *   2. Right pass with a running product of nums[i+1..n-1], multiplied into answer[i].
 *
 * KEY INSIGHT
 *   "Everything except i" = "everything left of i" combined with "everything right of i".
 *   Two linear passes compute both sides for every i, and the second side can live in a
 *   single variable, so the output array is the only extra memory.
 *
 * COMPLEXITY
 *   Time O(n), space O(1) besides the output (O(n) with explicit left/right arrays).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 42   Trapping Rain Water      water[i] = min(maxLeft, maxRight) - h[i];
 *                                            two pointers do it in O(1) space
 *   [coded] LC 849  Max Distance to Closest  distance to the last 1 on the left and the next
 *                                            1 on the right; edges count one side only
 *           LC 821  Shortest Distance to Char  same two passes as LC 849
 *           LC 1769 Min Operations Move Balls  left pass carries (balls, cost); right pass too
 *           LC 2483 Min Penalty for a Shop     prefix count of 'N' + suffix count of 'Y'
 *           LC 135  Candy                      left pass for rising, right pass for falling
 *                                              -> P098_BoundariesTwoPasses
 *
 * PITFALLS
 *   - Division fails on zeros and is banned anyway.
 *   - LC 42: the bars at the edges hold no water; the min of the two maxima decides.
 *   - LC 42 two pointers: move the side with the SMALLER max; its water is already known.
 *
 * DEEP DIVE
 *   C05_ProductExceptSelf, C06_BestSeat (01-Arrays),
 *   D01_TrappingRainWater (02-Two-Pointers-Sliding-Window)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.Arrays;

class PrefixSuffixTwoPasses {

    // Canonical LC 238.
    static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];
        answer[0] = 1;
        for (int i = 1; i < n; i++) {
            answer[i] = answer[i - 1] * nums[i - 1];     // product of everything left of i
        }
        int right = 1;
        for (int i = n - 1; i >= 0; i--) {
            answer[i] *= right;                          // times everything right of i
            right *= nums[i];
        }
        return answer;
    }

    // LC 42, the template version: explicit max-left and max-right arrays.
    static int trapTwoArrays(int[] h) {
        int n = h.length;
        if (n == 0) {
            return 0;
        }
        int[] maxLeft = new int[n];
        int[] maxRight = new int[n];
        maxLeft[0] = h[0];
        for (int i = 1; i < n; i++) {
            maxLeft[i] = Math.max(maxLeft[i - 1], h[i]);
        }
        maxRight[n - 1] = h[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            maxRight[i] = Math.max(maxRight[i + 1], h[i]);
        }
        int water = 0;
        for (int i = 0; i < n; i++) {
            water += Math.min(maxLeft[i], maxRight[i]) - h[i];
        }
        return water;
    }

    // LC 42, O(1) space: the side with the smaller max is bounded by that max already.
    static int trapTwoPointers(int[] h) {
        int left = 0;
        int right = h.length - 1;
        int maxLeft = 0;
        int maxRight = 0;
        int water = 0;
        while (left < right) {
            if (h[left] < h[right]) {
                maxLeft = Math.max(maxLeft, h[left]);
                water += maxLeft - h[left];
                left++;
            } else {
                maxRight = Math.max(maxRight, h[right]);
                water += maxRight - h[right];
                right--;
            }
        }
        return water;
    }

    // LC 849: for each empty seat, the closest person is the nearer of the two sides.
    static int maxDistToClosest(int[] seats) {
        int n = seats.length;
        int[] leftDist = new int[n];
        int last = -1;
        for (int i = 0; i < n; i++) {
            if (seats[i] == 1) {
                last = i;
            }
            leftDist[i] = last == -1 ? Integer.MAX_VALUE : i - last;
        }
        int best = 0;
        int next = -1;
        for (int i = n - 1; i >= 0; i--) {
            if (seats[i] == 1) {
                next = i;
            }
            int rightDist = next == -1 ? Integer.MAX_VALUE : next - i;
            if (seats[i] == 0) {
                best = Math.max(best, Math.min(leftDist[i], rightDist));
            }
        }
        return best;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 238 [1,2,3,4]",
                Arrays.toString(productExceptSelf(new int[]{1, 2, 3, 4})), "[24, 12, 8, 6]");
        check("LC 238 [-1,1,0,-3,3] one zero",
                Arrays.toString(productExceptSelf(new int[]{-1, 1, 0, -3, 3})), "[0, 0, 9, 0, 0]");
        check("LC 238 [0,0] two zeros",
                Arrays.toString(productExceptSelf(new int[]{0, 0})), "[0, 0]");

        int[] bars = {0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1};
        check("LC 42 two arrays", trapTwoArrays(bars), 6);
        check("LC 42 two pointers", trapTwoPointers(bars), 6);
        check("LC 42 [4,2,0,3,2,5] arrays", trapTwoArrays(new int[]{4, 2, 0, 3, 2, 5}), 9);
        check("LC 42 [4,2,0,3,2,5] pointers", trapTwoPointers(new int[]{4, 2, 0, 3, 2, 5}), 9);
        check("LC 42 [1,2,3] rising holds none", trapTwoPointers(new int[]{1, 2, 3}), 0);

        check("LC 849 [1,0,0,0,1,0,1]", maxDistToClosest(new int[]{1, 0, 0, 0, 1, 0, 1}), 2);
        check("LC 849 [1,0,0,0] right edge", maxDistToClosest(new int[]{1, 0, 0, 0}), 3);
        check("LC 849 [0,1] left edge", maxDistToClosest(new int[]{0, 1}), 1);
    }
}
