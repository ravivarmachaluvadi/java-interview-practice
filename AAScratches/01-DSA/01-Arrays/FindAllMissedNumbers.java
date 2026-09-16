/**
 * Finds all numbers in {@code [1..n]} that do not appear in the array, where
 * {@code n == nums.length}.
 *
 * <p>Uses the sign-marking technique: for each value {@code v}, negate the entry at
 * index {@code v - 1} to record "v was seen". Any index still holding a positive
 * value after the marking pass corresponds to a missing number.
 *
 * <p>Runs in O(n) time and O(1) extra space, but <b>mutates the input array</b>.
 *
 * <pre>
 * Input:  nums = [4, 3, 2, 7, 8, 2, 3, 1]
 * Output: [5, 6]
 * </pre>
 *
 * <ul>
 *   <li>{@code n == nums.length}</li>
 *   <li>{@code 1 <= nums[i] <= n}</li>
 * </ul>
 *
 * @param nums the input array, modified in place
 * @return the missing numbers in ascending order
 */
// https://leetcode.com/problems/find-all-numbers-disappeared-in-an-array/description/
// Constraints:
// n == nums.length
// 1 <= nums[i] <= n
// Cyclic Sort
public static List<Integer> findDisappearedNumbers(int[] nums) {
    int n = nums.length;
    ArrayList<Integer> list = new ArrayList<>();
    // use currVal to get correctIdx by math currVal-1
    // and make value at correctIdx as negatiive
    for (int i = 0; i < n; i++) {
        // Get the index for the number nums[i]
        int correctIndex = Math.abs(nums[i]) - 1;
        if (nums[correctIndex] > 0) {
            nums[correctIndex] = -nums[correctIndex];
        }
    }
    for (int j = 0; j < n; j++) {
        // If the number is still positive,
        // it means the  number (i+1) is missing
        if (nums[j] > 0) {
            list.add(j + 1);
        }
    }
    return list;
}

void main() {
    // Output: [5, 6]
    IO.println(findDisappearedNumbers(new int[]{4, 3, 2, 7, 8, 2, 3, 1}));
}

