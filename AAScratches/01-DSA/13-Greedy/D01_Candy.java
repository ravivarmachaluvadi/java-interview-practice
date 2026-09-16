/**
 * Example 1:
 * <p>
 * <p>
 * Input: ratings = [1,0,2]
 * <p>
 * Output: 5
 * <p>
 * Explanation: You can allocate to the first, second and third child with 2, 1, 2 candies respectively.
 * <p>
 * <p>
 * Example 2:
 * <p>
 * Input: ratings = [1,2,2]
 * <p>
 * Output: 4
 * <p>
 * Explanation: You can allocate to the first, second and third child with 1, 2, 1 candies respectively.
 * <p>
 * The third child gets 1 candy because it satisfies the above two conditions.
 */

// https://leetcode.com/problems/candy/
class Candy {
    public static int candy(int[] nums) {
        int[] left = new int[nums.length];
        left[0] = 1;
        int[] right = new int[nums.length];
        right[nums.length - 1] = 1;
        int ans = 0;
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] > nums[i - 1]) {
                left[i] = left[i - 1] + 1;
            } else {
                left[i] = 1;
            }
        }
        for (int i = nums.length - 2; i >= 0; i--) {
            if (nums[i] > nums[i + 1]) {
                right[i] = right[i + 1] + 1;
            } else {
                right[i] = 1;
            }
        }
        for (int i = 0; i < nums.length; i++) {
            ans += Math.max(left[i], right[i]);
        }
        return ans;
    }

    public static void main(String[] args) {
        System.out.println(candy(new int[]{1, 2, 2})); // 4
    }
}
