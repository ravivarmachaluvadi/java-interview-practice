import java.util.Stack;
/**
 * Input: heights = [2,1,5,6,2,3]
 * <p>
 * Output: 10
 */
// https://leetcode.com/problems/largest-rectangle-in-histogram/description/
class ImportantLargestRectangleArea {
    public static void main(String[] args) {
        int[] arr = {2, 1, 5, 6, 2, 3};
        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;
        int n = arr.length;

        for (int i = 0; i <= n; i++) {
            while (!stack.isEmpty() && (i == n || arr[stack.peek()] >= arr[i])) {
                int height = arr[stack.pop()];
                int width = stack.empty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, width * height);
            }
            stack.push(i);
        }
        System.out.println(maxArea);
    }
}

