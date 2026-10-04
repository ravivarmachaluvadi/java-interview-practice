/*
 * =====================================================================
 *  P038 Monotonic Stack: Boundaries and Contribution   Canonical LC 84 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 84, Largest Rectangle in Histogram)
 *   heights[i] is the height of a bar of width 1. Return the area of the largest
 *   rectangle that fits inside the histogram.
 *
 * EXAMPLE
 *   [2, 1, 5, 6, 2, 3]  ->  10     bars 5 and 6, height 5, width 2
 *   [2, 4]              ->  4
 *   [2, 2, 2]           ->  6      equal heights: the trap for strict comparisons
 *
 * RECOGNIZE WHEN
 *   - Each element "owns" the widest range where it is the minimum (or maximum), and the
 *     answer combines value x range: rectangles, "sum of subarray minimums", "count of
 *     subarrays where a[i] is the max".
 *   - Brute force is "for each i, expand left and right while neighbours are >= a[i]".
 *   Not this if: you only need the next greater / smaller index -> P037_NextGreaterElement.
 *
 * TEMPLATE
 *   stack = []                                       // indices with increasing heights
 *   for i in 0..n:                                   // i == n is a height-0 sentinel
 *       h = (i == n) ? 0 : a[i]
 *       while stack and a[stack.top] >= h:
 *           mid = stack.pop()
 *           left = stack.empty ? -1 : stack.top      // previous smaller (exclusive)
 *           // a[mid] is the minimum of every range (left, i) that contains mid
 *           use a[mid], left, i                      // area = a[mid] * (i - left - 1)
 *       stack.push(i)
 *
 * APPROACH
 *   1. Keep bar indices with increasing heights.
 *   2. A shorter bar ends the rectangle of every taller bar on the stack: pop it; its
 *      left limit is the new top, its right limit is i.
 *   3. A sentinel of height 0 at the end flushes the stack.
 *
 * KEY INSIGHT
 *   When a bar is popped, BOTH of its limits are known at once: the bar below it on the
 *   stack is the previous smaller, and the bar that popped it is the next smaller. So
 *   each element's "range where I am the minimum" costs O(1) amortised.
 *
 * COMPLEXITY
 *   Time O(n), space O(n). LC 85: O(rows * cols).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 907  Sum of Subarray Minimums contribution a[mid] * (mid - left) * (i - mid);
 *                                            pop on >= so equal values are counted once
 *   [coded] LC 85   Maximal Rectangle        each row builds a histogram of 1-heights;
 *                                            run LC 84 per row
 *   [coded] LC 42   Trapping Rain Water      pop a bottom; water = (min(left, right) -
 *                                            bottom) * width, layer by layer
 *           LC 2104 Sum of Subarray Ranges   sum of maxima minus sum of minima (twice)
 *           LC 1856 Max Min-Product          prefix sums + the same boundaries
 *           LC 1793 Max Score Good Subarray  LC 84 constrained to contain index k
 *
 * PITFALLS
 *   - Forgetting the sentinel leaves an increasing run on the stack, never measured.
 *   - Duplicates in LC 907: one side strict (<), the other non-strict (<=), or equal
 *     minimums are double counted.
 *   - LC 907 needs a modulo and long arithmetic.
 *
 * DEEP DIVE
 *   D02_LargestRectangleArea, C09_SumOfSubarrayMinimums (07-Stack-Queue-Monotonic),
 *   D01_TrappingRainWater (02-Two-Pointers-Sliding-Window)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Deque;

class StackBoundariesContribution {

    // Canonical LC 84.
    static int largestRectangleArea(int[] a) {
        Deque<Integer> stack = new ArrayDeque<>();
        int best = 0;
        for (int i = 0; i <= a.length; i++) {
            int h = i == a.length ? 0 : a[i];
            while (!stack.isEmpty() && a[stack.peek()] >= h) {
                int mid = stack.pop();
                int left = stack.isEmpty() ? -1 : stack.peek();
                best = Math.max(best, a[mid] * (i - left - 1));
            }
            stack.push(i);
        }
        return best;
    }

    // LC 907.
    static int sumSubarrayMins(int[] a) {
        final long mod = 1_000_000_007L;
        Deque<Integer> stack = new ArrayDeque<>();
        long total = 0;
        for (int i = 0; i <= a.length; i++) {
            int h = i == a.length ? Integer.MIN_VALUE : a[i];
            while (!stack.isEmpty() && a[stack.peek()] >= h) {
                int mid = stack.pop();
                int left = stack.isEmpty() ? -1 : stack.peek();
                total = (total + (long) a[mid] * (mid - left) * (i - mid)) % mod;
            }
            stack.push(i);
        }
        return (int) total;
    }

    // LC 85: heights[c] = consecutive '1's ending at this row in column c.
    static int maximalRectangle(char[][] matrix) {
        if (matrix.length == 0) {
            return 0;
        }
        int[] heights = new int[matrix[0].length];
        int best = 0;
        for (char[] row : matrix) {
            for (int c = 0; c < row.length; c++) {
                heights[c] = row[c] == '1' ? heights[c] + 1 : 0;
            }
            best = Math.max(best, largestRectangleArea(heights));
        }
        return best;
    }

    // LC 42 with a stack: fill water layer by layer above each popped bottom.
    static int trap(int[] h) {
        Deque<Integer> stack = new ArrayDeque<>();
        int water = 0;
        for (int i = 0; i < h.length; i++) {
            while (!stack.isEmpty() && h[stack.peek()] < h[i]) {
                int bottom = stack.pop();
                if (stack.isEmpty()) {
                    break;                         // no left wall
                }
                int left = stack.peek();
                int depth = Math.min(h[left], h[i]) - h[bottom];
                water += depth * (i - left - 1);
            }
            stack.push(i);
        }
        return water;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 84 [2,1,5,6,2,3]", largestRectangleArea(new int[]{2, 1, 5, 6, 2, 3}), 10);
        check("LC 84 [2,4]", largestRectangleArea(new int[]{2, 4}), 4);
        check("LC 84 [2,2,2] equal", largestRectangleArea(new int[]{2, 2, 2}), 6);
        check("LC 84 [1]", largestRectangleArea(new int[]{1}), 1);

        check("LC 907 [3,1,2,4]", sumSubarrayMins(new int[]{3, 1, 2, 4}), 17);
        check("LC 907 [11,81,94,43,3]", sumSubarrayMins(new int[]{11, 81, 94, 43, 3}), 444);
        check("LC 907 [2,2] duplicates", sumSubarrayMins(new int[]{2, 2}), 6);

        char[][] grid = {
                "10100".toCharArray(), "10111".toCharArray(),
                "11111".toCharArray(), "10010".toCharArray()};
        check("LC 85 4x5", maximalRectangle(grid), 6);
        check("LC 85 [[0]]", maximalRectangle(new char[][]{{'0'}}), 0);
        check("LC 85 [[1]]", maximalRectangle(new char[][]{{'1'}}), 1);

        check("LC 42 stack", trap(new int[]{0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1}), 6);
        check("LC 42 stack [4,2,0,3,2,5]", trap(new int[]{4, 2, 0, 3, 2, 5}), 9);
    }
}
