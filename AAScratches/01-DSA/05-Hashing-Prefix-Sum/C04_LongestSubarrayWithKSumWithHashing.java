import java.util.*;

class LongestSubarrayWithKSumWithHashing {
    // Math.max two times
    public static int getLongestSubarray(int[] a, long target) {
        int n = a.length;
        Map<Long, Integer> preSumMap = new HashMap<>();
        long preSum = 0;
        int maxLen = 0;
        for (int i = 0; i < n; i++) {
            preSum += a[i];
            if (preSum == target) {
                maxLen = Math.max(maxLen, i + 1);
            }
            // preSum-target
            long rem = preSum - target;

            if (preSumMap.containsKey(rem)) {
                maxLen = Math.max(maxLen, i - preSumMap.get(rem));
            }
            preSumMap.putIfAbsent(preSum, i);
        }

        return maxLen;
    }

    public static void main(String[] args) {
        int[] a = {2, 3, 5, 1, 9};
        long k = 10;
        int len = getLongestSubarray(a, k);
        System.out.println("The length of the longest subarray is: " + len);
    }

}
