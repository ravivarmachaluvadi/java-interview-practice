import java.util.Arrays;
import java.util.HashMap;

/**
 * Problem: Two Sum (LeetCode 1) - return indices of the ONE pair that adds to target,
 *          plus the sibling question "how MANY pairs add to target?" (same HashMap trick).
 * Approaches:
 *   - twoSumBruteForce : O(n^2) nested loop, no extra space. Fine for tiny arrays.
 *   - twoSumHashMap    : O(n) single pass, HashMap value -> index, look up (target - x).
 *   - countPairsWithSum: O(n) single pass, HashMap value -> frequency, count += freq(target - x).
 */
class TwoSum {

    // ---------- 1. Brute force: try every pair ----------
    // Simple, O(n^2) time, O(1) space. For very small arrays this can beat the HashMap
    // version because there is no hashing / boxing overhead.
    public static int[] twoSumBruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[0]; // no pair found (LeetCode guarantees exactly one, so unreachable there)
    }

    // ---------- 2. HashMap: value -> index ----------
    // One pass. Before storing nums[i], ask "have I already seen target - nums[i]?".
    // Checking BEFORE putting guarantees we never pair an element with itself.
    public static int[] twoSumHashMap(int[] nums, int target) {
        HashMap<Integer, Integer> seen = new HashMap<>(); // value -> index
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (seen.containsKey(complement)) {
                return new int[]{seen.get(complement), i};
            }
            seen.put(nums[i], i);
        }
        return new int[0]; // no pair found
    }

    // ---------- 3. Count ALL pairs with the given sum: value -> frequency ----------
    // Same idea, but the map stores how many times each value has appeared so far.
    // For each x, every earlier occurrence of (target - x) forms one new pair with x,
    // so count += freq(target - x). Then record x itself.
    // Duplicates are handled naturally: {1,5,7,-1,5}, target 6 -> (1,5) (7,-1) (1,5) = 3.
    // Time O(n), Space O(n).
    public static int countPairsWithSum(int[] nums, int target) {
        HashMap<Integer, Integer> freq = new HashMap<>(); // value -> occurrences seen so far
        int count = 0;
        for (int num : nums) {
            int complement = target - num;
            count += freq.getOrDefault(complement, 0);
            freq.put(num, freq.getOrDefault(num, 0) + 1); // record AFTER counting
        }
        return count;
    }

    public static void main(String[] args) {
        int[] nums = {1, 5, 7, -1, 5};
        int target = 6;
        System.out.println("nums = " + Arrays.toString(nums) + ", target = " + target);
        System.out.println("twoSumBruteForce  : " + Arrays.toString(twoSumBruteForce(nums, target)));
        System.out.println("twoSumHashMap     : " + Arrays.toString(twoSumHashMap(nums, target)));
        System.out.println("countPairsWithSum : " + countPairsWithSum(nums, target));

        // LeetCode sample: [2,7,11,15], target 9 -> [0,1]
        int[] lc = {2, 7, 11, 15};
        System.out.println("\nnums = " + Arrays.toString(lc) + ", target = 9");
        System.out.println("twoSumBruteForce  : " + Arrays.toString(twoSumBruteForce(lc, 9)));
        System.out.println("twoSumHashMap     : " + Arrays.toString(twoSumHashMap(lc, 9)));
        System.out.println("countPairsWithSum : " + countPairsWithSum(lc, 9));
    }
}
