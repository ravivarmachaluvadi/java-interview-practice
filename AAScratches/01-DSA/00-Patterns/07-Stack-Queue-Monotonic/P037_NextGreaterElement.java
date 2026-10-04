/*
 * =====================================================================
 *  P037 Monotonic Stack: Next Greater / Smaller   Canonical LC 739 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 739, Daily Temperatures)
 *   For each day, return how many days you must wait for a warmer temperature, or 0 if
 *   none comes.
 *
 * EXAMPLE
 *   [73, 74, 75, 71, 69, 72, 76, 73]  ->  [1, 1, 4, 2, 1, 1, 0, 0]
 *   [30, 40, 50, 60]                  ->  [1, 1, 1, 0]
 *   [30, 60, 90]                      ->  [1, 1, 0]
 *
 * RECOGNIZE WHEN
 *   - For EACH element: the nearest element to the right (or left) that is greater
 *     (or smaller). "next warmer day", "next bigger price", "previous higher building".
 *   - "span" / "how many consecutive days before today were <= today".
 *   - O(n^2) brute force is "scan right from every i until something is bigger".
 *   Not this if: you need the max of every fixed window -> P042_MonotonicDeque; you need
 *   the area / sum over ranges bounded by smaller elements -> P038_StackBoundariesContribution.
 *
 * TEMPLATE
 *   stack = []                                  // indices whose answer is still unknown;
 *                                               // their values are decreasing bottom->top
 *   for i in 0..n-1:
 *       while stack and a[stack.top] < a[i]:    // a[i] is the answer for everything smaller
 *           j = stack.pop(); answer[j] = i
 *       stack.push(i)
 *   // previous greater: same loop; after popping, stack.top (if any) is i's answer
 *   // next smaller: flip the comparison
 *
 * APPROACH
 *   1. Keep the days still waiting for a warmer day on a stack.
 *   2. Today pops every waiting day that is colder; their wait is today - thatDay.
 *   3. Push today; it waits too.
 *
 * KEY INSIGHT
 *   An element that is blocked by a later, bigger element can never be anyone's "next
 *   greater" for the elements further left, so it can be discarded the moment the bigger
 *   one arrives. What is left on the stack is always decreasing, and each index is pushed
 *   and popped once: O(n).
 *
 * COMPLEXITY
 *   Time O(n) (each index pushed and popped at most once), space O(n).
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 496  Next Greater Element I   run the template on nums2, store value -> next
 *                                            greater in a map, look up nums1
 *   [coded] LC 503  Next Greater Element II  circular: loop i over 0..2n-1, use i % n, push
 *                                            only in the first pass
 *   [coded] LC 901  Online Stock Span        PREVIOUS greater, online: stack of
 *                                            (price, span); pop and add spans while <= price
 *           LC 1475 Final Prices w/ Discount next SMALLER-OR-EQUAL element
 *           LC 2454 Next Greater Element IV  second greater: two stacks
 *           LC 1019 Next Greater Node in List copy the list to an array first
 *           LC 456  132 Pattern              scan right to left; stack + "best third" value
 *           LC 1944 Visible People in Queue  count pops, plus one if the stack is not empty
 *
 * PITFALLS
 *   - Push INDICES, not values, when you need distances.
 *   - "<" vs "<=" decides how equal values behave; read the problem twice.
 *   - Unresolved indices keep the default answer (0 or -1); set it up front.
 *
 * DEEP DIVE
 *   C06_DailyTemperatures, A03_NextGreaterElements, C10_Find132pattern,
 *   D03_VisiblePeopleInQueue (07-Stack-Queue-Monotonic)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

class NextGreaterElement {

    // Canonical LC 739.
    static int[] dailyTemperatures(int[] t) {
        int[] answer = new int[t.length];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < t.length; i++) {
            while (!stack.isEmpty() && t[stack.peek()] < t[i]) {
                int j = stack.pop();
                answer[j] = i - j;
            }
            stack.push(i);
        }
        return answer;
    }

    // LC 496: nums1 is a subset of nums2; all values distinct.
    static int[] nextGreaterElement(int[] nums1, int[] nums2) {
        Map<Integer, Integer> next = new HashMap<>();
        Deque<Integer> stack = new ArrayDeque<>();
        for (int x : nums2) {
            while (!stack.isEmpty() && stack.peek() < x) {
                next.put(stack.pop(), x);
            }
            stack.push(x);
        }
        int[] answer = new int[nums1.length];
        for (int i = 0; i < nums1.length; i++) {
            answer[i] = next.getOrDefault(nums1[i], -1);
        }
        return answer;
    }

    // LC 503: circular array; walk it twice.
    static int[] nextGreaterElements(int[] nums) {
        int n = nums.length;
        int[] answer = new int[n];
        Arrays.fill(answer, -1);
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < 2 * n; i++) {
            int x = nums[i % n];
            while (!stack.isEmpty() && nums[stack.peek()] < x) {
                answer[stack.pop()] = x;
            }
            if (i < n) {
                stack.push(i);
            }
        }
        return answer;
    }

    // LC 901: span = consecutive days up to today with price <= today's price.
    static class StockSpanner {
        private final Deque<int[]> stack = new ArrayDeque<>();   // {price, span}

        int next(int price) {
            int span = 1;
            while (!stack.isEmpty() && stack.peek()[0] <= price) {
                span += stack.pop()[1];           // absorb the days that one covered
            }
            stack.push(new int[]{price, span});
            return span;
        }
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 739 eight days",
                Arrays.toString(dailyTemperatures(new int[]{73, 74, 75, 71, 69, 72, 76, 73})),
                "[1, 1, 4, 2, 1, 1, 0, 0]");
        check("LC 739 rising",
                Arrays.toString(dailyTemperatures(new int[]{30, 40, 50, 60})), "[1, 1, 1, 0]");
        check("LC 739 equal temperatures wait",
                Arrays.toString(dailyTemperatures(new int[]{50, 50, 60})),
                "[2, 1, 0]");

        check("LC 496 [4,1,2] in [1,3,4,2]",
                Arrays.toString(nextGreaterElement(new int[]{4, 1, 2}, new int[]{1, 3, 4, 2})),
                "[-1, 3, -1]");
        check("LC 496 [2,4] in [1,2,3,4]",
                Arrays.toString(nextGreaterElement(new int[]{2, 4}, new int[]{1, 2, 3, 4})),
                "[3, -1]");

        check("LC 503 [1,2,1]",
                Arrays.toString(nextGreaterElements(new int[]{1, 2, 1})), "[2, -1, 2]");
        check("LC 503 [1,2,3,4,3]", Arrays.toString(nextGreaterElements(new int[]{1, 2, 3, 4, 3})),
                "[2, 3, 4, -1, 4]");

        StockSpanner spanner = new StockSpanner();
        int[] prices = {100, 80, 60, 70, 60, 75, 85};
        int[] spans = new int[prices.length];
        for (int i = 0; i < prices.length; i++) {
            spans[i] = spanner.next(prices[i]);
        }
        check("LC 901 seven prices", Arrays.toString(spans), "[1, 1, 1, 2, 1, 4, 6]");
    }
}
