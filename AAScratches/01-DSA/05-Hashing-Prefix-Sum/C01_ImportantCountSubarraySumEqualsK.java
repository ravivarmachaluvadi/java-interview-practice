import java.util.HashMap;
import java.util.Map;

// Array, Hash Table, Prefix Sum
// https://leetcode.com/problems/subarray-sum-equals-k

/**
 * Input: nums = [1,2,3], k = 3
 * <p>
 * Output: 2
 */
class ImportantCountSubarraySumEqualsK {
    public static int subarraySum(int[] nums, int k) {
        int count = 0;
        Map<Integer, Integer> preSumCountMap = new HashMap<>();
        int preSum = 0;
        for (int i = 0; i < nums.length; i++) {
            preSum += nums[i];
            if (preSum == k) count++;
            int remSum = preSum - k;
            if (preSumCountMap.containsKey(preSum - k)) {
                // remember we update count with count from map of remSum
                // not in map
                count += preSumCountMap.get(remSum);
            }
            // update count of preSum in map
            preSumCountMap.put(preSum, preSumCountMap.getOrDefault(preSum, 0) + 1);
        }
        return count;
    }

    public static void main(String[] args) {
        System.out.println(subarraySum(new int[]{1, 2, 3}, 3));
    }
}

