/**
 * Problem: Given an array where each element represents the maximum jump length
 * from that position, determine if it is possible to reach the last index.
 *
 * Approach: Iterate through the array while maintaining the furthest reachable
 * index (maxJump). If at any point the current index exceeds maxJump,
 * reaching the end is impossible. Otherwise update maxJump with the maximum
 * of its current value and i + nums[i]. If traversal completes, return true.
 *
 * Time Complexity: O(n), where n is the length of the array.
 * Space Complexity: O(1) – only a few integer variables are used.
 */
class CanJump {
    public static boolean canJump(int[] nums) {
        int maxJump = 0;
        for (int i = 0; i < nums.length; i++) {
            if (i > maxJump) return false;

            maxJump = Math.max(maxJump, i + nums[i]);
        }
        return true;
    }

    public static void main(String[] args) {
        int[] nums = {4, 3, 7, 1, 2};

        System.out.print("Array representing maximum jump from each index: ");
        for (int i = 0; i < nums.length; i++) {
            System.out.print(nums[i] + " ");
        }
        System.out.println();
        boolean ans = canJump(nums);

        if (ans) {
            System.out.println("It is possible to reach the last index.");
        } else {
            System.out.println("It is not possible to reach the last index.");
        }
    }
}
