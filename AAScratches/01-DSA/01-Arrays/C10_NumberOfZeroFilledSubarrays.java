// https://leetcode.com/problems/number-of-zero-filled-subarrays/description/
// 2348. Number of Zero-Filled Subarrays

/**
 * Input: nums = [1,3,0,0,2,0,0,4]
 * <p>
 * Output: 6
 * <p>
 * Explanation:
 * <p>
 * There are 4 occurrences of [0] as a subarray.
 * <p>
 * There are 2 occurrences of [0,0] as a subarray.
 * <p>
 * There is no occurrence of a subarray with a size more than 2 filled with 0. Therefore, we return 6.
 * <p>
 * 2348. Number of Zero-Filled Subarrays
 */
class NumberOfZeroFilledSubarrays {

    public long zeroFilledSubarray(int[] nums) {
        long cnt = 0, streak = 0;
        for (int num : nums) {
            streak = (num == 0) ? streak + 1 : 0;
            cnt += streak;
        }
        return cnt;
    }

    public static void main(String[] args) {
        // Sample test cases
        NumberOfZeroFilledSubarrays solution = new NumberOfZeroFilledSubarrays();
        int[][] testCases = {
                {1, 3, 0, 0, 2, 0, 0, 4}, // 6
                {0, 0, 0, 2, 0, 0}, // 9
                {2, 10, 2019}, // 0
                {0, 0, 0} // 6
        };

        for (int[] nums : testCases) {
            System.out.print("Input: ");
            System.out.print("[");
            for (int i = 0; i < nums.length; i++) {
                System.out.print(nums[i] + (i < nums.length - 1 ? ", " : ""));
            }
            System.out.print("] -> Output: ");
            System.out.println(solution.zeroFilledSubarray(nums));
        }
    }
}
