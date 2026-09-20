/*
 * =====================================================================
 *  Smallest Missing Non-negative Integer After Operations   LeetCode 2598 | Medium
 * =====================================================================
 *
 * PROBLEM
 *   You may add or subtract `value` to any element any number of times. Return the largest
 *   MEX you can reach: the smallest non-negative integer NOT present in the array after
 *   the operations. nums may contain negatives; 1 <= value <= 10^5.
 *
 * EXAMPLE
 *   nums = [1, -10, 7, 13, 6, 8], value = 5  ->  4    remainders {1,0,2,3,1,3}: 0,1,2,3 covered
 *   nums = [1, -10, 7, 13, 6, 8], value = 7  ->  2    remainders {1,4,0,6,6,1}: no rem 2
 *   nums = [0, 0, 0], value = 1              ->  3    every number is rem 0, three copies
 *   nums = [-1, -2], value = 3               ->  0    rem 2 and rem 1; nothing can become 0
 *
 * APPROACH  (remainder classes as an invariant + greedy consume in order)
 *   1. Adding or subtracting value never changes num mod value, so each element can be
 *      turned into ANY non-negative number of its remainder class, but only that class.
 *   2. Count elements per remainder. Use ((num % value) + value) % value so negatives map
 *      to 0..value-1 (Java's % keeps the sign of the dividend).
 *   3. Walk i = 0, 1, 2, ...  Target i needs an unused element with remainder i % value.
 *      If the bucket is empty, i is the answer. Otherwise consume one and continue.
 *   Equivalently: answer = min over r of (count[r] * value + r); the walk finds it greedily.
 *
 * KEY INSIGHT
 *   Spot the invariant first: +/- value cannot move an element out of its residue class.
 *   Once you see that, the problem stops being about the numbers and becomes "how many
 *   tokens of each remainder do I have", which is the same remainder bucketing as the
 *   PairsOfSongs problem. Pattern: an operation that preserves x mod k means "bucket by
 *   x mod k and reason about counts".
 *
 * COMPLEXITY
 *   Time  O(n + answer)  counting pass, then the walk stops at most n steps later
 *   Space O(min(n, value))  one map entry per distinct remainder
 *
 * INTERVIEW FOLLOW-UPS
 *   - Derive the closed form min(count[r] * value + r) and argue why the greedy walk
 *     returns exactly that value.
 *   - Why does the walk terminate? After n consumptions some bucket must be empty.
 *   - value is huge (10^9) with small n: the map still works; an int[value] would not.
 *   - Negative numbers: show why (-11 % 5 + 5) % 5 == 4 and plain -11 % 5 == -1 would break it.
 *
 * RUN
 *   main() runs 4 cases (two LeetCode examples, all-same remainder, negatives that never
 *   reach 0) and prints actual vs expected.
 */
import java.util.HashMap;
import java.util.Map;

class SmallestMissingNonNegativeIntegerAfterOperations {

    public static int findSmallestInteger(int[] nums, int value) {
        Map<Integer, Integer> remainderCount = new HashMap<>();
        for (int num : nums) {
            remainderCount.merge(nonNegativeMod(num, value), 1, Integer::sum);
        }

        // target i can only be produced by an element whose remainder is i % value
        int target = 0;
        while (true) {
            int rem = target % value;
            int available = remainderCount.getOrDefault(rem, 0);
            if (available == 0) return target;
            remainderCount.put(rem, available - 1); // spend one element on this target
            target++;
        }
    }

    /** Java's % keeps the dividend's sign: -11 % 5 == -1. Shift into 0..value-1. */
    private static int nonNegativeMod(int num, int value) {
        return ((num % value) + value) % value;
    }

    private static void print(String label, int[] nums, int value, int expected) {
        System.out.println(label + ": " + findSmallestInteger(nums, value)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical        ", new int[]{1, -10, 7, 13, 6, 8}, 5, 4);
        print("case 2 missing class  ", new int[]{1, -10, 7, 13, 6, 8}, 7, 2);
        print("case 3 all same rem   ", new int[]{0, 0, 0}, 1, 3);
        print("case 4 negatives, no 0", new int[]{-1, -2}, 3, 0);
    }
}
