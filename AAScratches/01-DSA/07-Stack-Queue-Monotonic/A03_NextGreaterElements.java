/*
 * =====================================================================
 *  Next Greater Element (array)                   LeetCode 496 family | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   For every element of an int array, find the first element to its RIGHT that is strictly
 *   greater. If none exists, output -1 for that position. Return the results as an array of
 *   the same length. Values may repeat.
 *
 * EXAMPLE
 *   [4, 5, 2, 10, 8]     ->  [5, 10, 10, -1, -1]
 *   [3, 7, 1, 7, 6, 4]   ->  [7, -1, 7, -1, -1, -1]   equal 7 does not count as greater
 *   [6, 8, 0, 1, 3]      ->  [8, -1, 1, 3, -1]
 *   [5, 4, 3]            ->  [-1, -1, -1]              strictly decreasing, nothing resolves
 *   []                   ->  []                        used to throw, see Fixed below
 *
 * APPROACH  (Monotonic decreasing stack of indices)
 *   1. Keep a stack of INDICES whose next greater element is still unknown. The values at
 *      those indices are always decreasing from bottom to top.
 *   2. For each i: while the stack top's value is smaller than nums[i], that top has just
 *      found its answer, nums[i]. Record it and pop.
 *   3. Push i. It waits for something bigger to arrive.
 *   4. After the loop, everything left on the stack never saw a bigger value: set -1.
 *
 * KEY INSIGHT
 *   An element that is smaller than the newcomer can never be anyone's "next greater", so it
 *   can be resolved and discarded right now. That is why the stack stays decreasing and why
 *   every index is pushed once and popped once. Recognise the pattern whenever a question asks
 *   "for each element, the nearest larger/smaller to the left/right".
 *   Fixed: the original pushed index 0 before the loop, so an empty array threw
 *   ArrayIndexOutOfBounds. The loop now starts at 0 and handles empty input naturally.
 *
 * COMPLEXITY
 *   Time  O(n)  each index is pushed and popped at most once, so the while loop is O(n) total
 *   Space O(n)  the stack plus the output array
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the DISTANCE instead of the value: store indices (as here) and use i - top.
 *     That is C06_DailyTemperatures.
 *   - Circular array (LeetCode 503): loop i from 0 to 2n-1 and index with i % n.
 *   - Next SMALLER element: flip the comparison, the stack becomes increasing.
 *   - Previous greater element: same loop, but the answer for i is stack.peek() after popping.
 *
 * RUN
 *   main() runs 5 cases (typical, duplicates, mixed, strictly decreasing, empty) and prints
 *   actual vs expected.
 */
import java.util.Arrays;
import java.util.Stack;

class NextGreaterElements {

    static int[] getNextGreaterElements(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        // Indices still waiting for their next greater element; values decrease bottom to top
        Stack<Integer> waiting = new Stack<>();

        for (int i = 0; i < n; i++) {
            // nums[i] is the answer for every waiting index whose value is strictly smaller
            while (!waiting.isEmpty() && nums[waiting.peek()] < nums[i]) {
                result[waiting.pop()] = nums[i];
            }
            waiting.push(i);
        }

        // Whatever is still waiting never met a bigger value
        while (!waiting.isEmpty()) {
            result[waiting.pop()] = -1;
        }
        return result;
    }

    static void print(String label, int[] input, String expected) {
        int[] actual = getNextGreaterElements(input);
        System.out.println(label + " " + Arrays.toString(input) + " -> " + Arrays.toString(actual)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical   ", new int[]{4, 5, 2, 10, 8}, "[5, 10, 10, -1, -1]");
        print("case 2 duplicates", new int[]{3, 7, 1, 7, 6, 4}, "[7, -1, 7, -1, -1, -1]");
        print("case 3 mixed     ", new int[]{6, 8, 0, 1, 3}, "[8, -1, 1, 3, -1]");
        print("case 4 decreasing", new int[]{5, 4, 3}, "[-1, -1, -1]");
        print("case 5 empty     ", new int[]{}, "[]");
    }
}
