// LeetCode Problem: https://leetcode.com/problems/missing-ranges/

import java.util.ArrayList;
import java.util.List;

/**
 * Input: nums = [0,1,3,50,75], lower = 0, upper = 99
 * Output: [[2,2],[4,49],[51,74],[76,99]]
 */
class MissingRanges {
    public List<List<Integer>> findMissingRanges(int[] nums, int lower, int upper) {
        List<List<Integer>> missingRanges = new ArrayList<>();
        int n = nums.length;
        if (n == 0) {
            missingRanges.add(List.of(lower, upper));
            return missingRanges;
        }
        // Check if there's a range before the first element
        // edge case
        if (lower < nums[0]) {
            missingRanges.add(List.of(lower, nums[0] - 1));
        }
        // Find ranges between the numbers in the array
        for (int i = 0; i < n - 1; i++) {
            if (nums[i + 1] - nums[i] <= 1) {
                continue;
            }
            missingRanges.add(List.of(nums[i] + 1, nums[i + 1] - 1));
        }
        // Check if there's a range after the last element
        // edge case
        if (upper > nums[n - 1]) {
            missingRanges.add(List.of(nums[n - 1] + 1, upper));
        }
        return missingRanges;
    }

    public static void main(String[] args) {
        MissingRanges solution = new MissingRanges();
        int[] nums = {0, 1, 3, 50, 75};
        int lower = 0;
        int upper = 99;

        List<List<Integer>> result = solution.findMissingRanges(nums, lower, upper);
        System.out.println("Missing ranges: " + result);
        // [[2, 2], [4, 49], [51, 74], [76, 99]]

    }
}
