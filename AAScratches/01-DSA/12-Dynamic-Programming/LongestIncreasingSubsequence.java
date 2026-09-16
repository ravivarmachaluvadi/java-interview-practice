import java.util.ArrayList;

// https://leetcode.com/problems/longest-increasing-subsequence/
// 300. Longest Increasing Subsequence
class LongestIncreasingSubsequence {
    static int maxLen = 0;

    public static void main(String[] args) {
//        int[] nums = {10, 9, 2, 5, 3, 7, 101, 18};
        int[] nums = {7, 7, 7, 7, 7, 7, 7};

        ArrayList<Integer> list = new ArrayList<>();
        getLengthLongestIncreasingSubsequence(nums, 0, list);
        System.out.println("maxLen : " + maxLen);
    }

    private static void getLengthLongestIncreasingSubsequence(int[] nums, int i, ArrayList<Integer> list) {
        if (i == nums.length) {
            if (list.size() > maxLen) {
                maxLen = list.size();
            }
            return;
        }

        if (list.isEmpty() || list.get(list.size() - 1) < nums[i]) {
            list.add(nums[i]);
            getLengthLongestIncreasingSubsequence(nums, i + 1, list);
            list.remove(list.size() - 1);
        }
        getLengthLongestIncreasingSubsequence(nums, i + 1, list);

    }
}
