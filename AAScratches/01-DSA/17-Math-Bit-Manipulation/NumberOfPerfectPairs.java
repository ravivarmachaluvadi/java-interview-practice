import java.util.Arrays;

// https://leetcode.com/problems/number-of-perfect-pairs/description/
// 3649. Number of Perfect Pairs
class NumberOfPerfectPairs {
    public static long perfectPairs(int[] nums) {

        // a - +, b - +   a - b <= min(a, b)
        // a - -, b - -, a - b <= min(a, b)
        // a - + , b - -
        // a - -, b - +
        // observation => num of pairs that hold a - b <= min(a, b)

        int n = nums.length;

        for (int i = 0; i < n; i++) nums[i] = Math.abs(nums[i]);

        Arrays.sort(nums);

        int pt = 0;
        long ans = 0;

        for (int i = 0; i < n; i++) {
            while (nums[i] - nums[pt] > nums[pt]) pt++;
            ans += i - pt;
            // System.out.println(pt);
        }
        return ans;
    }
    public static void main(String[] args) {
        // Example test case
        int[] nums = {-3, 1, 2, -2, 4};
        // Explanation: abs values => {1,2,2,3,4}
        // Pairs (i<j) that satisfy the condition are counted by the method
        long result = perfectPairs(nums);
        System.out.println("Number of perfect pairs = " + result);

        // More tests
        int[] nums2 = {0, 0, 0};
        System.out.println("Expected pairs for {0,0,0} -> 3  ; Actual = " + perfectPairs(nums2));

        int[] nums3 = {5, -1, 3, -2};
        System.out.println("For {5,-1,3,-2} -> Actual = " + perfectPairs(nums3));
    }
}
