import java.util.ArrayList;
import java.util.Collections;

/**
 * <p>Solves the <b>Leaders in an Array</b> problem.</p>
 *
 * <p>An element is a <i>leader</i> if it is strictly greater than every
 * element to its right. The last element of the array is always a leader.
 * Returns all leaders in the order they appear in the original array.</p>
 *
 * <p><b>Approach:</b></p>
 * <ul>
 *   <li>Scan from right to left, tracking the maximum seen so far.</li>
 *   <li>Whenever the current element exceeds that running max, it's a
 *       leader — add it and update the max.</li>
 *   <li>Since leaders are discovered right-to-left, reverse the list at the
 *       end to restore original left-to-right order.</li>
 * </ul>
 *
 * <pre>
 * Input:  [16, 17, 4, 3, 5, 2]
 * Output: [17, 5, 2]
 * </pre>
 *
 * <p>Time complexity: {@code O(n)}. Space complexity: {@code O(k)} for the
 * output, where {@code k} is the number of leaders.</p>
 */
class LeadersInAnArray {

    /**
     * <p>Finds all leaders in {@code nums}, in their original left-to-right order.</p>
     *
     * @param nums the input array (may be empty)
     * @return an {@code ArrayList} of leaders, in original array order
     */
    public ArrayList<Integer> leaders(int[] nums) {
        ArrayList<Integer> ans = new ArrayList<>();
        int n = nums.length;
        if (n == 0) return ans;

        // Last element of the array is always a leader
        int max = nums[n - 1];
        ans.add(nums[n - 1]);

        // Check elements from right to left
        for (int i = n - 2; i >= 0; i--) {
            if (nums[i] > max) {
                ans.add(nums[i]);
                max = nums[i];
            }
        }
        /* Reverse the list to match
        the required output order */
        Collections.reverse(ans);
        // Return the leaders
        return ans;
    }

    public static void main(String[] args) {
        LeadersInAnArray obj = new LeadersInAnArray();

        int[] arr1 = {16, 17, 4, 3, 5, 2};
        System.out.println("Leaders (Example 1): " + obj.leaders(arr1)); // [17, 5, 2]

        int[] arr2 = {1, 2, 3, 4, 5};
        System.out.println("Leaders (Example 2): " + obj.leaders(arr2)); // [5]

        int[] arr3 = {5, 4, 3, 2, 1};
        System.out.println("Leaders (Example 3): " + obj.leaders(arr3)); // [5, 4, 3, 2, 1]

        int[] arr4 = {};
        System.out.println("Leaders (Example 4): " + obj.leaders(arr4)); // []
    }
}