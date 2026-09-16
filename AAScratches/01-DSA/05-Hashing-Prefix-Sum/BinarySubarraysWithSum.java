import java.util.HashMap;
import java.util.Map;

// https://leetcode.com/problems/binary-subarrays-with-sum/description/
// 930. Binary Subarrays With Sum
class BinarySubarraysWithSum {
    public static int numSubarraysWithSum(int[] nums, int goal) {
        Map<Integer, Integer> cnt = new HashMap<>();
        cnt.put(0, 1);

        int preSum = 0;
        int result = 0;
        for (int num : nums) {
            preSum += num;
            // how many previous prefix sums equal s-goal?
            int want = preSum - goal;
            result += cnt.getOrDefault(want, 0);
            // record current prefix sum
            cnt.put(preSum, cnt.getOrDefault(preSum, 0) + 1);
        }

        return result;
    }

    public static void main(String[] args) {
        // Example 1
        int[] nums1 = {1, 0, 1, 0, 1};
        int goal1 = 2;
        System.out.println("Example 1 result: " + numSubarraysWithSum(nums1, goal1));
        // Expected output: 4

        // Example 2
        int[] nums2 = {0, 0, 0, 0, 0};
        int goal2 = 0;
        System.out.println("Example 2 result: " + numSubarraysWithSum(nums2, goal2));
        // Expected output: 15
    }
}
