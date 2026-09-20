/*
 * =====================================================================
 *  Trapping Rain Water                                 LeetCode 42 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   height[i] is the height of a bar of width 1. After it rains, water sits in the dips
 *   between taller bars. Return the total units of water trapped.
 *   Water above bar i = min(tallest bar to its left, tallest bar to its right) - height[i],
 *   never negative.
 *
 * EXAMPLE
 *   [1, 5, 3, 2, 7, 8, 9, 0, 4, 2, 3]       ->  10   2 + 3 over the 3,2 dip; 4 + 1 on the right
 *   [0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1]    ->  6    the classic LeetCode picture
 *   [4, 2, 0, 3, 2, 5]                      ->  9
 *   [1, 2, 3]                               ->  0    monotonic, nothing is enclosed
 *   []                                      ->  0    no bars
 *
 * APPROACH  (converging pointers with running maxima)
 *   1. left = 0, right = n - 1, maxLeft = maxRight = 0, water = 0.
 *   2. While left <= right, look at the SHORTER side:
 *      - if height[left] <= height[right]: the left bar is bounded on its right by at
 *        least height[right] >= height[left], so its water is maxLeft - height[left]
 *        (or it becomes the new maxLeft). Move left forward.
 *      - else: symmetric on the right using maxRight. Move right back.
 *   3. Return water.
 *
 * KEY INSIGHT
 *   You normally need BOTH the left max and the right max for a bar, which is why the
 *   two-array prefix/suffix solution exists. The trick: when height[left] <= height[right],
 *   the right side is guaranteed to have a bar at least as tall as the left one, so
 *   min(maxLeft, maxRight) for the left bar is just maxLeft. The shorter side's answer is
 *   fully determined without ever knowing the true right max. This is Container With
 *   Most Water's "move the shorter wall" argument, extended to a per-bar computation.
 *
 * COMPLEXITY
 *   Time  O(n)  each pointer moves toward the other, one step per iteration
 *   Space O(1)  two indices and two running maxima
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prefix/suffix max arrays version: O(n) time, O(n) space; explain first, then optimise.
 *   - Monotonic stack version: fills water horizontally layer by layer as you pop.
 *   - 2D version (Trapping Rain Water II, 407): BFS from the border with a min-heap.
 *   - Prove why the pointer on the shorter side can be settled without seeing the far max.
 *
 * RUN
 *   main() runs 5 cases (typical, edge, tricky) and prints actual vs expected.
 */
import java.util.Arrays;

class ImportantTrappingRainWater {

    public static int trap(int[] heights) {
        int left = 0, right = heights.length - 1;
        int maxLeft = 0, maxRight = 0;
        int water = 0;

        while (left <= right) {
            if (heights[left] <= heights[right]) {
                // right side has a bar >= heights[left], so maxLeft alone bounds this bar
                if (heights[left] >= maxLeft) {
                    maxLeft = heights[left];
                } else {
                    water += maxLeft - heights[left];
                }
                left++;
            } else {
                // symmetric: left side has a bar > heights[right], so maxRight alone bounds it
                if (heights[right] >= maxRight) {
                    maxRight = heights[right];
                } else {
                    water += maxRight - heights[right];
                }
                right--;
            }
        }
        return water;
    }

    private static void print(int[] heights, int expected) {
        System.out.println(Arrays.toString(heights) + ": " + trap(heights)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print(new int[]{1, 5, 3, 2, 7, 8, 9, 0, 4, 2, 3}, 10);
        print(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}, 6);
        print(new int[]{4, 2, 0, 3, 2, 5}, 9);
        print(new int[]{1, 2, 3}, 0);
        print(new int[]{}, 0);
    }
}
