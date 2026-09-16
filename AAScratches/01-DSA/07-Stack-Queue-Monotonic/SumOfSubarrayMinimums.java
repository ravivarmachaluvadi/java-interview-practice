import java.util.*;

// 907. Sum of Subarray Minimums
// https://leetcode.com/problems/sum-of-subarray-minimums/description/
class SumOfSubarrayMinimums {
    private static final int MOD = 1_000_000_007; // (10 ^ 8 + 7)

    public static int sumSubarrayMins(int[] arr) {
        int n = arr.length;
        int[] prev = new int[n];
        int[] next = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();

        // Previous smaller element
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && arr[stack.peek()] > arr[i]) {
                stack.pop();
            }
            prev[i] = stack.isEmpty() ? -1 : stack.peek();
            stack.push(i);
        }

        // Clear stack to reuse
        stack.clear();

        // Next smaller or equal element
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && arr[stack.peek()] >= arr[i]) {
                stack.pop();
            }
            next[i] = stack.isEmpty() ? n : stack.peek();
            stack.push(i);
        }

        long result = 0;
        for (int i = 0; i < n; i++) {
            long leftCount = i - prev[i];
            long rightCount = next[i] - i;
            result = (result + arr[i] * leftCount % MOD * rightCount % MOD) % MOD;
        }
        return (int) result;
    }

    public static void main(String[] args) {
        int[] example = {3, 1, 2, 4};
        int output = sumSubarrayMins(example);
        System.out.println("Input: " + Arrays.toString(example));
        System.out.println("Output: " + output);
        // Expected: 17
    }
}
