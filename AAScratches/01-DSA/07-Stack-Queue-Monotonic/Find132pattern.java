import java.util.Stack;
/**
 * The "132 Pattern" problem is about finding a subsequence of three integers
 * <p>
 * in an array such that they follow a "132" pattern. Specifically,
 * <p>
 * we need to find i, j, and k with i < j < k where nums[i] < nums[k] < nums[j].
 */

// https://leetcode.com/problems/132-pattern/description/
class Find132pattern {
    public static boolean find132pattern(int[] nums) {
        int n = nums.length;
        if (n < 3) return false;

        Stack<Integer> stack = new Stack<>();
        int second = Integer.MIN_VALUE; // This will hold the `nums[k]` value in the "132" pattern.

        // Traverse from right to left
        for (int i = n - 1; i >= 0; i--) {
            // Check if we have a "132" pattern
            // 1 , 6 , 5 , 4 , 3 , 2
            if (nums[i] < second) {
                System.out.println(nums[i] + " " + second);
                return true;
            }

            // Maintain the decreasing stack stack and update `second`
            // 1 , 6 , 5 , 4 , 3 , 2  at 6 second= 2 next iteration 1 < 2
            while (!stack.isEmpty() && nums[i] > stack.peek()) {
                second = stack.pop();
            }
            // Push current element as a potential `nums[i]` candidate
            stack.push(nums[i]);
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(find132pattern(new int[]{1, 2, 3, 4})); // false
        System.out.println(find132pattern(new int[]{3, 1, 6, 5, 4, 3, 2})); // true
        System.out.println(find132pattern(new int[]{4, 3, 2, 1})); // false
    }
}
