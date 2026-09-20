/*
 * =====================================================================
 *  Two Sum                                    LeetCode 1 | Easy    MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an integer array nums and an integer target, return the indices of the
 *   two elements that add up to target. LeetCode guarantees exactly one answer and
 *   you may not use the same element twice. This file also answers the sibling
 *   question "how MANY pairs (i < j) add up to target?" with the same trick.
 *
 * EXAMPLE
 *   nums = [2, 7, 11, 15], target = 9   ->  [0, 1]   because 2 + 7 = 9
 *   nums = [1, 5, 7, -1, 5], target = 6 ->  [0, 1]   and 3 pairs: (1,5) (7,-1) (1,5)
 *   nums = [3, 3], target = 6           ->  [0, 1]   same value twice is allowed
 *   nums = [1, 2], target = 5           ->  []       no pair (count 0)
 *
 * APPROACH  (Hash map complement lookup)
 *   1. Keep a HashMap value -> index of the elements already scanned.
 *   2. For each nums[i], compute complement = target - nums[i].
 *   3. If complement is already in the map, the answer is [map.get(complement), i].
 *   4. Otherwise put nums[i] -> i and move on.
 *   The check happens BEFORE the put, so an element can never pair with itself.
 *   twoSumBruteForce is the O(n^2) baseline; countPairsWithSum stores value -> count
 *   instead of value -> index and adds freq(complement) for every element.
 *
 * KEY INSIGHT
 *   Instead of searching for a partner (O(n) per element), ask the map whether the
 *   partner has ALREADY been seen (O(1)). One pass, "look back, then record".
 *   This exact shape (scan, look up what you need, then store what you have) is
 *   the template for subarray-sum, pair-count and anagram-grouping problems.
 *
 * COMPLEXITY
 *   Time  O(n)  one pass, O(1) average per map operation (brute force: O(n^2))
 *   Space O(n)  the map holds up to n entries
 *
 * INTERVIEW FOLLOW-UPS
 *   - Input sorted? Two pointers from both ends gives O(n) time with O(1) space (LC 167).
 *   - Return values instead of indices, or all pairs? Sort + two pointers, skip duplicates.
 *   - Three Sum / Four Sum: fix one element, reduce to Two Sum on the rest (LC 15, 18).
 *   - Count pairs with duplicates: value -> frequency map, as countPairsWithSum shows.
 *
 * RUN
 *   main() runs 4 cases (LeetCode sample, duplicates, same value twice, no pair) and
 *   prints actual vs expected for all three methods.
 */
import java.util.Arrays;
import java.util.HashMap;

class TwoSum {

    // ---------- 1. Brute force: try every pair ----------
    // O(n^2) time, O(1) space. For very small arrays this can beat the HashMap version
    // because there is no hashing / boxing overhead.
    public static int[] twoSumBruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[0]; // no pair found (unreachable on LeetCode, which guarantees one)
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

    // ---------- 3. Count ALL pairs (i < j) with the given sum: value -> frequency ----------
    // Same idea, but the map stores how many times each value has appeared so far.
    // For each x, every earlier occurrence of (target - x) forms one new pair with x,
    // so count += freq(target - x). Then record x itself.
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

    private static void check(String label, int[] nums, int target,
                              String expectedPair, int expectedCount) {
        System.out.println(label + " nums = " + Arrays.toString(nums) + ", target = " + target);
        System.out.println("   brute force : " + Arrays.toString(twoSumBruteForce(nums, target))
                + "   expected " + expectedPair);
        System.out.println("   hash map    : " + Arrays.toString(twoSumHashMap(nums, target))
                + "   expected " + expectedPair);
        System.out.println("   pair count  : " + countPairsWithSum(nums, target)
                + "   expected " + expectedCount);
    }

    public static void main(String[] args) {
        check("case 1 (LeetCode sample):", new int[]{2, 7, 11, 15}, 9, "[0, 1]", 1);
        check("case 2 (duplicates):     ", new int[]{1, 5, 7, -1, 5}, 6, "[0, 1]", 3);
        check("case 3 (same value x2):  ", new int[]{3, 3}, 6, "[0, 1]", 1);
        check("case 4 (no pair):        ", new int[]{1, 2}, 5, "[]", 0);
    }
}
