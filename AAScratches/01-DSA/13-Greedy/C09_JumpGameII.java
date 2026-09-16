// 45. Jump Game II
// https://leetcode.com/problems/jump-game-ii/description/

/**
 * Return the minimum number of jumps to reach index n - 1.
 * <p>
 * The test cases are generated such that you can reach index n - 1.
 */
class JumpGameII {

    public int jump(int[] nums) {
        int n = nums.length;
        if (n <= 1) return 0;

        int jumps = 0;
        int currentEnd = 0;
        int farthest = 0;

        for (int i = 0; i < n - 1; i++) {
            farthest = Math.max(farthest, i + nums[i]);

            if (i == currentEnd) {
                jumps++;
                currentEnd = farthest;
                if (currentEnd >= n - 1) break;
            }
        }
        return jumps;
    }

    public static void main(String[] args) {
        JumpGameII sol = new JumpGameII();

        int[] arr1 = {2, 3, 1, 1, 4};
        System.out.println("Minimum jumps for [2,3,1,1,4]: " + sol.jump(arr1)); // Expected: 2

        int[] arr2 = {2, 1};
        System.out.println("Minimum jumps for [2,1]: " + sol.jump(arr2)); // Expected: 1

        int[] arr3 = {3, 2, 1, 0, 4};
        System.out.println("Minimum jumps for [3,2,1,0,4]: " + sol.jump(arr3)); // Expected: not reachable in normal logic, but your method gives 3 (since it doesn’t detect traps)
    }
}
