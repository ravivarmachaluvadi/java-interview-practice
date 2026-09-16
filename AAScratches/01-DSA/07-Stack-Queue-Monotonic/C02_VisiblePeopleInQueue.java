import java.util.*;

// https://leetcode.com/problems/number-of-visible-people-in-a-queue/description/
// 1944. Number of Visible People in a Queue
class VisiblePeopleInQueue {
    /**
     * Distinct integers
     * Input: heights = [10,6,8,5,11,9]
     * Output: [3,1,2,1,1,0]
     * <p>
     * the ith person can see the jth person if i < j and
     * min(heights[i], heights[j]) > max(heights[i+1], heights[i+2], ..., heights[j-1])
     * {2, 3, 4, 5, 11, 12}
     */
    public static int[] canSeePersonsCount(int[] heights) {
        int n = heights.length;
        int[] result = new int[n];
        Deque<Integer> stack = new ArrayDeque<>();
        // Process from right to left
        for (int i = n - 1; i >= 0; i--) {
            // Count the people shorter than the current person whom he can see
            while (!stack.isEmpty() && stack.peekFirst() <= heights[i]) {
                stack.pop();
                result[i]++;
            }
            // still If the stack is not empty, they
            // can also see one taller person because
            if (!stack.isEmpty()) {
                result[i]++;
            }
            // Add the current height to the stack
            stack.addFirst(heights[i]);
        }
        return result;
    }

    public static void main(String[] args) {
        int[] heights = {10, 6, 8, 5, 11, 9};
        int[] result = canSeePersonsCount(heights);

        System.out.println("Input heights: " + Arrays.toString(heights));
        //Output: [3,1,2,1,1,0]
        System.out.println("Visible people count: " + Arrays.toString(result));

        int[] heights1 = {2, 3, 4, 5, 11, 12};
        int[] result1 = canSeePersonsCount(heights1);
        System.out.println("Input heights: " + Arrays.toString(heights1));
        //[1, 1, 1, 1, 1, 0]
        System.out.println("Visible people count: " + Arrays.toString(result1));
    }
}
