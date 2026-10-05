/*
 * =====================================================================
 *  Largest Rectangle in Histogram               LeetCode 84 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given bar heights (width 1 each), return the area of the largest rectangle
 *   that fits entirely inside the histogram. Heights are non-negative and the
 *   array can be empty or a single bar.
 *
 * EXAMPLE
 *   [2, 1, 5, 6, 2, 3]  ->  10   bars 5 and 6, height 5 x width 2
 *   [2, 2, 2]           ->  6    all equal, height 2 x width 3
 *   [5, 4, 3, 2, 1]     ->  9    height 3 x width 3 (bars 5,4,3)
 *   []                  ->  0 [1]                 ->  1
 *
 *   The first example drawn: R = the largest rectangle, height 5 x width 2 = 10.
 *
 *                #
 *             R  R
 *             R  R
 *             R  R     #
 *       #     R  R  #  #
 *       #  #  R  R  #  #
 *       2  1  5  6  2  3    <- height
 *       0  1  2  3  4  5    <- index
 *
 * APPROACH  (monotonic increasing stack of indices)
 *   1. Walk i from 0 to n inclusive; treat i == n as a bar of height 0 so
 *      every bar still on the stack gets flushed at the end.
 *   2. While the bar at the stack top is >= the current bar, pop it: the
 *      current bar is its first shorter bar on the RIGHT, and the new stack
 *      top is its first shorter bar on the LEFT.
 *   3. Width for the popped bar = i - stackTop - 1 (or i if the stack is
 *      empty, meaning nothing shorter to the left). Area = height x width.
 *   4. Push i. The stack stays increasing in height bottom to top.
 *
 *   The drawn example, step by step. A popped bar's rectangle runs between its
 *   two shorter neighbours: from the bar now on top of the stack to i.
 *
 *      i  h[i]  pop: height x width = area      stack after (index(height))
 *      0   2    -                             0(2)
 *      1   1    0(2): 2 x 1 = 2  new best     1(1)
 *      2   5    -                             1(1) 2(5)
 *      3   6    -                             1(1) 2(5) 3(6)
 *      4   2    3(6): 6 x 1 = 6  new best     1(1) 4(2)
 *               2(5): 5 x 2 = 10  new best
 *      5   3    -                             1(1) 4(2) 5(3)
 *      6  end   5(3): 3 x 1 = 3               (empty)
 *               4(2): 2 x 4 = 8
 *               1(1): 1 x 6 = 6
 *
 * KEY INSIGHT
 *   Every bar is the limiting height of exactly one maximal rectangle, and
 *   that rectangle spans from the previous shorter bar to the next shorter
 *   bar. The stack finds both boundaries in one pass: the pop moment gives the
 *   right boundary, the element beneath gives the left. Width is the distance
 *   between the two neighbours minus one, the same arithmetic as
 *   Longest Valid Parentheses. Popping on >= (not just >) is safe: an equal
 *   bar computes a too-narrow width but the last equal bar computes the full one.
 *
 * COMPLEXITY
 *   Time  O(n)  each index pushed once and popped once
 *   Space O(n)  the stack in the worst case (increasing heights)
 *
 * INTERVIEW FOLLOW-UPS
 *   - Maximal Rectangle in a binary matrix (LC 85): run this per row on a
 *     column-height histogram.
 *   - Trapping Rain Water (LC 42): same stack, different arithmetic.
 *   - Two-pass version: prevSmaller[] and nextSmaller[] arrays, then a loop.
 *   - Why not brute force O(n^2) with a running min? Works, but fails at 1e5.
 *
 * RUN
 *   main() runs 5 cases (typical, all equal, decreasing, empty, single)
 *   and prints actual vs expected.
 *   In the Code Viewer, Visual (Alt+V) plays the approach step by step on any
 *   input (not in Practice mode).
 */

import java.util.Arrays;
import java.util.Stack;

class ImportantLargestRectangleArea {

    public static int largestRectangleArea(int[] heights) {
        Stack<Integer> increasing = new Stack<>(); // indices, heights increasing bottom to top
        int maxArea = 0;
        int n = heights.length;

        for (int i = 0; i <= n; i++) {
            // i == n acts as a sentinel bar of height 0 that flushes the stack
            while (!increasing.isEmpty() && (i == n || heights[increasing.peek()] >= heights[i])) {
                int height = heights[increasing.pop()];
                // left boundary = new stack top (previous shorter bar), right boundary = i
                int width = increasing.isEmpty() ? i : i - increasing.peek() - 1;
                maxArea = Math.max(maxArea, width * height);
            }
            increasing.push(i);
        }
        return maxArea;
    }

    static void print(String label, int[] heights, int expected) {
        System.out.println(label + " " + Arrays.toString(heights) + ": "
                + largestRectangleArea(heights) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical   ", new int[]{2, 1, 5, 6, 2, 3}, 10);
        print("case 2 all equal ", new int[]{2, 2, 2}, 6);
        print("case 3 decreasing", new int[]{5, 4, 3, 2, 1}, 9);
        print("case 4 empty     ", new int[]{}, 0);
        print("case 5 single    ", new int[]{1}, 1);
    }
}
