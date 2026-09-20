/*
 * =====================================================================
 *  Container With Most Water                 LeetCode 11 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   height[i] is the height of a vertical wall at x = i. Pick two walls so that
 *   the water held between them (width * shorter wall) is as large as possible.
 *   Return that area. n >= 2, heights are non-negative.
 *
 * EXAMPLE
 *   height = [1, 8, 6, 2, 5, 4, 8, 3, 7]  ->  49   walls at i=1 and i=8: 7 * min(8,7)
 *   height = [1, 1]                       ->  1    only one pair exists
 *   height = [4, 3, 2, 1, 4]              ->  16   the outermost pair is already best
 *   height = [1, 2, 1]                    ->  2    ends give 2*1; middle pairs give 1*1
 *
 * APPROACH  (greedy converge, drop the shorter wall)
 *   1. left = 0, right = n - 1: start with the widest container possible.
 *   2. area = (right - left) * min(height[left], height[right]); keep the max.
 *   3. Move the pointer on the SHORTER wall inward (ties: either side is fine).
 *   4. Stop when the pointers meet. The max seen is the answer.
 *
 * KEY INSIGHT
 *   Moving inward always loses width, so the only hope of a bigger area is a
 *   taller limiting wall. The shorter wall is the limit; keeping it and moving
 *   the taller one can never help (area stays capped by the short wall and the
 *   width shrinks). So every pair we skip is provably no better than one we
 *   already measured. Pattern: "converging pointers + exchange argument" - the
 *   same reasoning powers Trapping Rain Water.
 *
 * COMPLEXITY
 *   Time  O(n)   each step moves one pointer inward; at most n - 1 steps
 *   Space O(1)   two indices and a running max
 *
 * INTERVIEW FOLLOW-UPS
 *   - Prove the greedy: why is it safe to never revisit the discarded wall?
 *   - Trapping Rain Water (LC 42): same two pointers but track running maxima.
 *   - What if heights are equal? Moving either side is fine; show why.
 *   - Return the indices of the best pair, not just the area.
 *
 * RUN
 *   main() runs 4 cases (typical, two elements, outer pair best, equal ends) and
 *   prints actual vs expected.
 */
class ContainerWithMostWater {

    public static int maxArea(int[] height) {
        int maxArea = 0;
        int left = 0;
        int right = height.length - 1;

        while (left < right) {
            int width = right - left;
            int limitingWall = Math.min(height[left], height[right]);
            maxArea = Math.max(maxArea, width * limitingWall);

            // The shorter wall caps the area; it can never do better, so drop it.
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }
        return maxArea;
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical    ", maxArea(new int[]{1, 8, 6, 2, 5, 4, 8, 3, 7}), 49);
        print("case 2 two walls  ", maxArea(new int[]{1, 1}), 1);
        print("case 3 outer pair ", maxArea(new int[]{4, 3, 2, 1, 4}), 16);
        print("case 4 equal ends ", maxArea(new int[]{1, 2, 1}), 2);
    }
}
