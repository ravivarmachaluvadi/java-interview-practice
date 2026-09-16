import java.util.HashMap;
import java.util.Map;

// https://leetcode.com/problems/smallest-missing-non-negative-integer-after-operations/description/
// 2598. Smallest Missing Non-negative Integer After Operations
class SmallestMissingNonNegativeIntegerAfterOperations {
    public int findSmallestInteger(int[] nums, int value) {
        Map<Integer, Integer> remainderCount = new HashMap<>();
        for (int num : nums) {
            int rem = ((num % value) + value) % value;
            // ((-11%5)+5)%5 == 4
            remainderCount.put(rem, remainderCount.getOrDefault(rem, 0) + 1);
        }

        int i = 0;
        while (true) {
            int rem = i % value;
            int c = remainderCount.getOrDefault(rem, 0);
            if (c == 0) {
                return i;
            }
            // use one element with this remainder
            remainderCount.put(rem, c - 1);
            i++;
        }
    }

    public static void main(String[] args) {
        SmallestMissingNonNegativeIntegerAfterOperations sol = new SmallestMissingNonNegativeIntegerAfterOperations();

        int[] nums1 = {1, -10, 7, 13, 6, 8};
        int value1 = 5;
        System.out.println("Example 1 → " + sol.findSmallestInteger(nums1, value1));
        // Expected output: 4

        int[] nums2 = {1, -10, 7, 13, 6, 8};
        int value2 = 7;
        System.out.println("Example 2 → " + sol.findSmallestInteger(nums2, value2));
        // Expected output: 2
    }
}
