/*
 * =====================================================================
 *  Sum of Subarray Minimums                      LeetCode 907 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array arr, return the sum of min(b) over every contiguous subarray b,
 *   modulo 1e9 + 7. Up to 3e4 elements, so enumerating all O(n^2) subarrays is too slow.
 *
 * EXAMPLE
 *   [3, 1, 2, 4]          ->  17    mins: 3,1,2,4, 1,1,2, 1,1, 1  =  17
 *   [11, 81, 94, 43, 3]   ->  444
 *   [2, 2, 2]             ->  12    6 subarrays, every min is 2 (tie-break must not double count)
 *   [5]                   ->  5
 *
 * APPROACH  (previous smaller-or-equal + next strictly smaller, count contributions)
 *   1. prev[i]: index of the nearest element to the LEFT that is smaller OR EQUAL (-1 if none).
 *      Left-to-right monotonic increasing stack, pop while arr[top] > arr[i].
 *   2. next[i]: index of the nearest element to the RIGHT that is STRICTLY smaller (n if none).
 *      Right-to-left stack, pop while arr[top] >= arr[i].
 *   3. arr[i] is the minimum of every subarray that starts in (prev[i], i] and ends in
 *      [i, next[i]). That is (i - prev[i]) * (next[i] - i) subarrays.
 *   4. Answer = sum of arr[i] * leftCount * rightCount, taking the modulus at every step.
 *
 * KEY INSIGHT
 *   Flip the question: instead of "what is the min of each subarray", ask "how many subarrays
 *   is each element the min of". That turns O(n^2) subarrays into O(n) contributions.
 *   Ties: use strict on one side and non-strict on the other. For equal values, the LEFT copy
 *   owns the subarrays spanning both; if both sides were strict, those would be counted twice,
 *   and if both non-strict, counted by nobody.
 *
 * COMPLEXITY
 *   Time  O(n)  three linear passes; each index is pushed and popped at most once
 *   Space O(n)  prev[], next[] and the stack
 *
 * INTERVIEW FOLLOW-UPS
 *   - Sum of Subarray Ranges (LeetCode 2104): run the same thing for maximums and subtract.
 *   - Single pass version: compute the contribution at pop time instead of storing prev/next.
 *   - Why long and the mod inside the product? arr[i] * left * right can exceed int and even
 *     long if the mod is only applied at the end.
 *
 * RUN
 *   main() runs 4 cases (typical, LeetCode example 2, all equal, single) and prints
 *   actual vs expected.
 */

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

class SumOfSubarrayMinimums {
    private static final int MOD = 1_000_000_007; // 10^9 + 7

    public static int sumSubarrayMins(int[] arr) {
        int n = arr.length;
        int[] prevSmallerOrEqual = previousSmallerOrEqual(arr);
        int[] nextStrictlySmaller = nextStrictlySmaller(arr);

        long total = 0;
        for (int i = 0; i < n; i++) {
            long leftChoices = i - prevSmallerOrEqual[i];      // valid start indices
            long rightChoices = nextStrictlySmaller[i] - i;   // valid end indices
            total = (total + arr[i] * leftChoices % MOD * rightChoices % MOD) % MOD;
        }
        return (int) total;
    }

    /** prev[i] = nearest index on the left with arr[j] <= arr[i], or -1. Non-strict. */
    private static int[] previousSmallerOrEqual(int[] arr) {
        int n = arr.length;
        int[] prev = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && arr[stack.peek()] > arr[i]) {
                stack.pop();
            }
            prev[i] = stack.isEmpty() ? -1 : stack.peek();
            stack.push(i);
        }
        return prev;
    }

    /** next[i] = nearest index on the right with arr[j] < arr[i], or n. Strict on purpose. */
    private static int[] nextStrictlySmaller(int[] arr) {
        int n = arr.length;
        int[] next = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) {
                stack.pop();
            }
            next[i] = stack.isEmpty() ? n : stack.peek();
            stack.push(i);
        }
        return next;
    }

    private static void print(String label, int[] input, int expected) {
        System.out.println(label + ": " + Arrays.toString(input) + " -> "
                + sumSubarrayMins(input) + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical   ", new int[]{3, 1, 2, 4}, 17);
        print("case 2 example 2 ", new int[]{11, 81, 94, 43, 3}, 444);
        print("case 3 all equal ", new int[]{2, 2, 2}, 12);
        print("case 4 single    ", new int[]{5}, 5);
    }
}
