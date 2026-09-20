/*
 * =====================================================================
 *  Binary Subarrays With Sum                          LeetCode 930 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   nums contains only 0s and 1s. Return the number of non-empty contiguous subarrays whose
 *   sum equals goal. n up to 3 * 10^4, goal from 0 to n. goal = 0 is the case to get right:
 *   a run of zeros contributes many subarrays.
 *
 * EXAMPLE
 *   nums = [1, 0, 1, 0, 1], goal = 2  ->  4    ([1,0,1], [1,0,1,0], [0,1,0,1], [1,0,1])
 *   nums = [0, 0, 0, 0, 0], goal = 0  ->  15   all 5+4+3+2+1 subarrays sum to 0
 *   nums = [1, 1, 1],       goal = 4  ->  0    goal larger than the total
 *   nums = [1],             goal = 1  ->  1
 *
 * APPROACH  (prefix sum + count map, the LC 560 template)
 *   1. Seed the map with {0: 1} for the empty prefix.
 *   2. For each element, add it to prefixSum and add count[prefixSum - goal] to the answer:
 *      that many earlier prefixes close a subarray summing to goal.
 *   3. Record prefixSum in the map after the lookup.
 *
 * APPROACH 2  (sliding window, atMost(goal) - atMost(goal - 1))
 *   1. Because every element is >= 0, "subarrays with sum <= g" is countable with a window:
 *      expand right, shrink left while the window sum exceeds g, and each right end adds
 *      (right - left + 1) subarrays ending there.
 *   2. exactly(goal) = atMost(goal) - atMost(goal - 1). atMost(-1) is 0 by definition.
 *
 * KEY INSIGHT
 *   Same prefix-sum + map skeleton as LC 560, unchanged; a 0/1 array is just a special case.
 *   The non-negativity is what unlocks the O(1)-space alternative: "exactly k" is hard for a
 *   window, but "at most k" is monotonic, so subtract two at-most counts.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass (map) or two passes (window); each pointer moves at most n times
 *   Space O(n)  for the map;  O(1) for the sliding-window version
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why can't the window count "exactly goal" directly? (with zeros, many windows share a sum)
 *   - Subarrays with exactly k odd numbers (LC 1248): map odd -> 1, even -> 0, same code.
 *   - Subarrays with exactly k distinct values (LC 992): same atMost(k) - atMost(k-1) trick.
 *
 * RUN
 *   main() runs 4 cases (typical, all zeros with goal 0, unreachable goal, single element)
 *   through both methods and prints actual vs expected.
 */
import java.util.HashMap;
import java.util.Map;

class BinarySubarraysWithSum {

    /** Approach 1: prefix sum + count map. Works for any integers, not only 0/1. */
    public static int numSubarraysWithSum(int[] nums, int goal) {
        Map<Integer, Integer> prefixSumCount = new HashMap<>();
        prefixSumCount.put(0, 1); // empty prefix

        int prefixSum = 0;
        int result = 0;
        for (int num : nums) {
            prefixSum += num;
            // how many earlier prefixes equal prefixSum - goal? each one closes a valid subarray
            result += prefixSumCount.getOrDefault(prefixSum - goal, 0);
            prefixSumCount.merge(prefixSum, 1, Integer::sum);
        }
        return result;
    }

    /** Approach 2: sliding window, exactly(goal) = atMost(goal) - atMost(goal - 1). O(1) space. */
    public static int numSubarraysWithSumWindow(int[] nums, int goal) {
        return countAtMost(nums, goal) - countAtMost(nums, goal - 1);
    }

    /** Number of subarrays with sum <= limit. Valid only because every element is >= 0. */
    private static int countAtMost(int[] nums, int limit) {
        if (limit < 0) return 0;
        int left = 0, windowSum = 0, count = 0;
        for (int right = 0; right < nums.length; right++) {
            windowSum += nums[right];
            while (windowSum > limit) {
                windowSum -= nums[left++];
            }
            // every subarray ending at right and starting in [left, right] has sum <= limit
            count += right - left + 1;
        }
        return count;
    }

    private static void run(String label, int[] nums, int goal, int expected) {
        System.out.println(label + " map    : " + numSubarraysWithSum(nums, goal)
                + "   expected " + expected);
        System.out.println(label + " window : " + numSubarraysWithSumWindow(nums, goal)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 (typical)", new int[]{1, 0, 1, 0, 1}, 2, 4);
        run("case 2 (all zeros, goal 0)", new int[]{0, 0, 0, 0, 0}, 0, 15);
        run("case 3 (unreachable goal)", new int[]{1, 1, 1}, 4, 0);
        run("case 4 (single element)", new int[]{1}, 1, 1);
    }
}
