/*
 * =====================================================================
 *  Daily Temperatures                            LeetCode 739 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an array of daily temperatures, return an array where answer[i] is how many days
 *   you must wait after day i for a strictly warmer temperature. If no warmer day ever comes,
 *   answer[i] = 0. Up to 1e5 days, so the O(n^2) "scan right for each day" is too slow.
 *
 * EXAMPLE
 *   [73, 74, 75, 71, 69, 72, 76, 73]  ->  [1, 1, 4, 2, 1, 1, 0, 0]
 *   [50, 50, 50]                      ->  [0, 0, 0]   equal is not warmer
 *   [90, 80, 70]                      ->  [0, 0, 0]   never gets warmer
 *   [42]                              ->  [0]
 *
 * APPROACH  (monotonic decreasing stack of indices)
 *   1. Walk left to right. The stack holds indices of days still waiting for a warmer day;
 *      their temperatures decrease from bottom to top.
 *   2. For day i, while the index on top has a colder temperature than temps[i], pop it:
 *      day i is its answer, so result[top] = i - top.
 *   3. Push i. It now waits for its own warmer day.
 *   4. Whatever is left on the stack never found a warmer day; result stays 0 for them.
 *
 * KEY INSIGHT
 *   This is Next Greater Element, but the answer is a DISTANCE, so store indices, not values.
 *   Each index is pushed once and popped at most once, which is why the nested while loop is
 *   still linear. Pattern to recognise: "for each element, the nearest larger/smaller element
 *   to the left/right" is always a monotonic stack.
 *
 * COMPLEXITY
 *   Time  O(n)  every index is pushed and popped at most once
 *   Space O(n)  the stack, worst case on strictly decreasing input
 *
 * INTERVIEW FOLLOW-UPS
 *   - O(1) extra space: walk right to left and hop through result[] (j = i + 1, then
 *     j += result[j]) until temps[j] > temps[i] or result[j] == 0.
 *   - Return the warmer day's index or temperature instead of the distance.
 *   - Circular array: Next Greater Element II, loop 2n times and index with i % n.
 *
 * RUN
 *   main() runs 4 cases (typical, all equal, never warmer, single) and prints actual vs expected.
 */

import java.util.Arrays;
import java.util.Stack;

class DailyTemperatures {

    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        // Indices of days still waiting for a warmer day; temperatures decrease bottom to top.
        Stack<Integer> waiting = new Stack<>();
        for (int today = 0; today < n; today++) {
            // Today answers every waiting day that is strictly colder than today.
            while (!waiting.isEmpty() && temperatures[today] > temperatures[waiting.peek()]) {
                int colderDay = waiting.pop();
                result[colderDay] = today - colderDay;
            }
            waiting.push(today);
        }
        return result;
    }

    private static void print(String label, int[] actual, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(actual)
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        DailyTemperatures solution = new DailyTemperatures();

        print("case 1 typical      ",
                solution.dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73}),
                new int[]{1, 1, 4, 2, 1, 1, 0, 0});
        print("case 2 all equal    ",
                solution.dailyTemperatures(new int[]{50, 50, 50}),
                new int[]{0, 0, 0});
        print("case 3 never warmer ",
                solution.dailyTemperatures(new int[]{90, 80, 70}),
                new int[]{0, 0, 0});
        print("case 4 single day   ",
                solution.dailyTemperatures(new int[]{42}),
                new int[]{0});
    }
}
