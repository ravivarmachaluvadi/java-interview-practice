import java.util.HashMap;

class ImportantLongestSubarrayWithSumKHash {
    public int longestSubarrayWithSumK(int[] nums, int k) {
        // HashMap to store the first occurrence of each cumulative sum
        HashMap<Integer, Integer> sumIndexMap = new HashMap<>();
        int maxLength = 0;
        int cumulativeSum = 0; // cumulativeSum == preSum or prefixSum
        for (int i = 0; i < nums.length; i++) {
            cumulativeSum += nums[i];

            // Check if the cumulative sum itself equals k
            if (cumulativeSum == k) maxLength = i + 1;

            // Check if there's a previous cumulative sum that results in the sum k
            int remSum = cumulativeSum - k; // complimemnt sum
            if (sumIndexMap.containsKey(cumulativeSum - k)) {
                int length = i - sumIndexMap.get(key);
                maxLength = Math.max(maxLength, length);
            }
            // Store the first occurrence of this cumulative sum
            sumIndexMap.putIfAbsent(cumulativeSum, i);
        }
        return maxLength;
    }
}
