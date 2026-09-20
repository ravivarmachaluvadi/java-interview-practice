/*
 * =====================================================================
 *  Recursive Array Sum                                  Building block | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given an int array, return the sum of its elements - but written as a recursion
 *   instead of a for-loop. Values may be negative. An empty array sums to 0.
 *
 * EXAMPLE
 *   nums = [3, 5, -2, 7]  ->  13    because 3 + 5 + (-2) + 7
 *   nums = []             ->  0     nothing to add
 *   nums = [-4]           ->  -4    single element, straight to the base case
 *
 * APPROACH  (index-driven recursion)
 *   1. The public method hides the bookkeeping and starts the recursion at index 0.
 *   2. Base case: once the index walks past the last slot there is nothing left, so
 *      return 0 - the identity for addition.
 *   3. Recursive case: sum(nums, i) = nums[i] + sum(nums, i + 1). Take responsibility
 *      for exactly one element and hand the rest of the array to the next call.
 *
 * KEY INSIGHT
 *   The index parameter IS the loop counter, moved from the loop header into the call.
 *   "Handle element i, then recurse on i + 1" is the template every later file reuses -
 *   Subsets, CombinationSum and WordSearch all decide something about position i and
 *   then recurse on the rest. Learn to spot the pair: a public wrapper with the clean
 *   signature, and a private helper carrying the state.
 *
 * COMPLEXITY
 *   Time  O(n)  each index is visited exactly once.
 *   Space O(n)  n stack frames - strictly worse than the loop's O(1), which is exactly
 *               why you would still ship the loop in production code.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Rewrite it as divide and conquer (split in half); what changes? (stack becomes
 *     O(log n), which is why mergesort-shaped recursion is safe on huge inputs)
 *   - What happens on an array of 1,000,000 elements? (StackOverflowError)
 *   - Adapt it to return the max instead of the sum - what is the new base case?
 *   - Why must the base case return 0 and not, say, nums[nums.length - 1]?
 *
 * RUN
 *   main() runs 3 cases (typical, empty, single negative) and prints actual vs expected.
 */
import java.util.Arrays;

class RecursiveArraySum {

    /** Public entry point: hides the index so callers see a clean signature. */
    public int arraySum(int[] nums) {
        return sum(nums, 0);
    }

    private int sum(int[] nums, int index) {
        // Base case: walked past the end, nothing left to add.
        if (index >= nums.length) return 0;
        // Recursive case: own one element, delegate the rest of the array.
        return nums[index] + sum(nums, index + 1);
    }

    public static void main(String[] args) {
        RecursiveArraySum solver = new RecursiveArraySum();

        int[] typical = {3, 5, -2, 7};
        int[] empty = {};
        int[] single = {-4};

        print("case 1 (typical) " + Arrays.toString(typical), solver.arraySum(typical), 13);
        print("case 2 (empty)   " + Arrays.toString(empty), solver.arraySum(empty), 0);
        print("case 3 (single)  " + Arrays.toString(single), solver.arraySum(single), -4);
    }

    private static void print(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }
}
