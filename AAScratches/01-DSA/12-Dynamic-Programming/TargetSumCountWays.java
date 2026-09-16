import java.util.*;

/**
 * Function to count the number
 * <p>
 * of ways to achieve the target sum
 */
class TargetSumCountWays {
    private int func(int ind, int target, int[] arr) {
        // Base case
        if (ind == 0) {
            if (target == 0 && arr[0] == 0)
                // as currVal is zero including
                // and not including results same
                return 2;
            if (target == 0 || target == arr[0])
                return 1;
            return 0;
        }

        int notTaken = func(ind - 1, target, arr);
        int taken = 0;
        if (arr[ind] <= target)
            taken = func(ind - 1, target - arr[ind], arr);
        // Return the sum of ways
        return (notTaken + taken);
    }

    public int targetSum(int n, int target, int[] nums) {
        int totSum = 0;

        for (int i = 0; i < nums.length; i++)
            totSum += nums[i];

        if (totSum - target < 0) return 0;

        if ((totSum - target) % 2 == 1) return 0;

        int s2 = (totSum - target) / 2;

        return func(n - 1, s2, nums);
    }

    public static void main(String[] args) {
        int[] nums = {1, 2, 3, 1};
        int target = 3;
        int n = nums.length;

        // Create an instance of Solution class
        TargetSumCountWays sol = new TargetSumCountWays();

        // Print the result
        System.out.println("The total number of ways is " + sol.targetSum(n, target, nums));
    }
}
