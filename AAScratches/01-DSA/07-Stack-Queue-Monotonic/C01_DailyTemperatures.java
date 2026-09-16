import java.util.Stack;

// 739. Daily Temperatures
/**
 * Example 1:
 * <p>
 * Input: temperatures = [73,74,75,71,69,72,76,73]
 * <p>
 * Output: [1,1,4,2,1,1,0,0]
 */
class DailyTemperatures {
    // 1 1 4 2 1 1 0 0
    // it's variation of next greater elements
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        // monotonic increasing stack
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < n; i++) {
            // pop stack as long as we are finding lower temperatures than current
            // and keep updating result array with no of days from i-indexes from stack
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int lastIndex = stack.pop();
                // Calculate the number of days waited
                result[lastIndex] = i - lastIndex;
            }
            stack.push(i);
        }
        return result;
    }

    public static void main(String[] args) {
        DailyTemperatures solution = new DailyTemperatures();
        int[] temperatures = {73, 74, 75, 71, 69, 72, 76, 73};
        int[] result = solution.dailyTemperatures(temperatures);
        for (int days : result) {
            System.out.print(days + " ");
        }
    }
}
