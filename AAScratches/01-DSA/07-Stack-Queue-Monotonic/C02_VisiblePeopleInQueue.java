/*
 * =====================================================================
 *  Number of Visible People in a Queue          LeetCode 1944 | Hard
 * =====================================================================
 *
 * PROBLEM
 *   People stand in a queue facing right; heights[i] is the height of person i and all heights
 *   are distinct. Person i can see person j (i < j) when everyone strictly between them is
 *   shorter than both of them. Return answer[i] = how many people person i can see to the right.
 *
 * EXAMPLE
 *   [10, 6, 8, 5, 11, 9]  ->  [3, 1, 2, 1, 1, 0]   person 0 sees 6, 8 and 11; 11 blocks 9
 *   [2, 3, 4, 5, 11, 12]  ->  [1, 1, 1, 1, 1, 0]   each person only sees the next (taller) one
 *   [5, 4, 3, 2, 1]       ->  [1, 1, 1, 1, 0]      each sees exactly the next (shorter) one
 *   [7]                   ->  [0]
 *
 * APPROACH  (monotonic decreasing stack, right to left, count the pops)
 *   1. Walk from the right end. The stack holds heights of people to the right of i that are
 *      still "visible from somewhere"; they decrease from bottom to top.
 *   2. While the top is shorter than heights[i], pop it and count it: person i sees them, and
 *      nobody further left will ever see them again because i is taller and stands in front.
 *   3. If the stack is not empty after popping, the top is taller than i: i can see that one
 *      person too (nothing between them is taller than i), so add 1 more. Stop there.
 *   4. Push heights[i].
 *
 * KEY INSIGHT
 *   Person i sees every shorter person up to and including the first taller one. The popped
 *   count is the "shorter" part; the +1 is the "first taller" part. The stack stays strictly
 *   decreasing, so each person is popped once, and the same stack answers everybody.
 *
 * COMPLEXITY
 *   Time  O(n)  each height is pushed once and popped at most once
 *   Space O(n)  the stack, worst case on strictly increasing input
 *
 * INTERVIEW FOLLOW-UPS
 *   - What changes if heights can repeat? Define whether equal heights block the view and
 *     switch the pop condition between < and <= accordingly.
 *   - Return WHO each person sees (store indices instead of heights and collect the pops).
 *   - Same shape as Daily Temperatures and Next Greater Element, only the bookkeeping differs.
 *
 * RUN
 *   main() runs 4 cases (typical, increasing, decreasing, single) and prints actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

class VisiblePeopleInQueue {

    public static int[] canSeePersonsCount(int[] heights) {
        int n = heights.length;
        int[] result = new int[n];
        // Heights to the right of i that are still visible; strictly decreasing from bottom to top.
        Deque<Integer> tallerToTheRight = new ArrayDeque<>();

        for (int i = n - 1; i >= 0; i--) {
            // Every shorter person on top is visible to i and blocked from everyone left of i.
            // Heights are distinct, so <= and < behave the same here.
            while (!tallerToTheRight.isEmpty() && tallerToTheRight.peek() <= heights[i]) {
                tallerToTheRight.pop();
                result[i]++;
            }
            // The first taller person is still visible; nobody taller than i stands in between.
            if (!tallerToTheRight.isEmpty()) {
                result[i]++;
            }
            tallerToTheRight.push(heights[i]);
        }
        return result;
    }

    private static void print(String label, int[] actual, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(actual)
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        print("case 1 typical    ", canSeePersonsCount(new int[]{10, 6, 8, 5, 11, 9}),
                new int[]{3, 1, 2, 1, 1, 0});
        print("case 2 increasing ", canSeePersonsCount(new int[]{2, 3, 4, 5, 11, 12}),
                new int[]{1, 1, 1, 1, 1, 0});
        print("case 3 decreasing ", canSeePersonsCount(new int[]{5, 4, 3, 2, 1}),
                new int[]{1, 1, 1, 1, 0});
        print("case 4 single     ", canSeePersonsCount(new int[]{7}),
                new int[]{0});
    }
}
