import java.util.Stack;

// String, Dynamic Programming, Stack
// https://leetcode.com/problems/longest-valid-parentheses

/**
 * Example 2:
 * <p>
 * Input: s = ")()())"
 * <p>
 * Output: 4
 * <p>
 * Explanation: The longest valid parentheses substring is "()()".
 */
class ImportantLongestValidParentheses {
    public static int longestValidParenthesesUsingStack(String s) {
        Stack<Integer> stack = new Stack<>();
        // edge case for started with ")))"
        // if empty stack it will throw empty stack exception
        stack.push(-1);
        int max_len = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(') {
                stack.push(i);
            } else {
                // edge case for started with ")))"
                // if empty stack it will throw empty stack exception
                // though we popped missed one covered with -1
                stack.pop();
                // edge case for started with ")))"
                // if empty stack it will throw empty stack exception
                if (stack.isEmpty()) {
                    stack.push(i);
                } else {
                    max_len = Math.max(max_len, i - stack.peek());
                }
            }
        }
        return max_len;
    }

    public static int longestValidParentheses(String s) {
        int left = 0;
        int right = 0;
        int max = 0;

        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '(')
                left++;
            else
                right++;

            if (left == right) {
                max = Math.max(max, left * 2);
            }
            //(())) right greater passed valid length and in invalid zone
            else if (right > left) {
                left = 0;
                right = 0;
            }
        }
        left = 0;
        right = 0;
        // same above logic copy-paste from right to left
        for (int i = s.length() - 1; i >= 0; i--) {
            if (s.charAt(i) == '(')
                left++;
            else
                right++;

            if (left == right) {
                max = Math.max(max, left * 2);
            } else if (left > right) {
                left = 0;
                right = 0;
            }
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println(longestValidParentheses(")(()()))"));
    }
}
