class LongestSubarrayAfterDeletingOne {

    public static int longestSubarray(int[] nums) {
        boolean zeroFound = false;
        int prevCount = 0;
        int currCount = 0;
        int res = 0;
        int n = nums.length;

        for (int num : nums) {
            if (num == 0) {
                zeroFound = true;
                res = Math.max(res, prevCount + currCount);
                prevCount = currCount;
                currCount = 0;
            } else
                currCount++;
        }

        res = Math.max(res, prevCount + currCount);
        if (!zeroFound)
            return n - 1;
        return res;
    }

    public static void main(String[] args) {
        int[] nums = {1, 1, 0, 1, 1, 1, 0, 1};
        int result = longestSubarray(nums);
        System.out.println("Longest subarray of 1s after deleting one element: " + result);
    }
}
