/*
 * =====================================================================
 *  First Missing Positive                        LeetCode 41 | Hard   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Given an unsorted int array nums (may contain negatives, zeros, duplicates and
 *   huge values), return the smallest positive integer that is not present.
 *   Required: O(n) time and O(1) auxiliary space. Rearranging nums in place is allowed.
 *
 * EXAMPLE
 *   nums = [3, 4, -1, 1]      ->  2   1 is present, 2 is not
 *   nums = [1, 2, 3, 4, 5]    ->  6   fully packed, answer is n + 1
 *   nums = [7, 8, 9, 11, 12]  ->  1   everything out of range
 *   nums = [1, 1]             ->  2   duplicate would loop without the swap guard
 *   nums = [2, 2]             ->  1   duplicate, and 1 never appears
 *   nums = []                 ->  1   empty input
 *
 * APPROACH  (cyclic sort with pigeonhole bound)
 *   1. n slots can hold at most the values 1..n, so the answer is in [1, n + 1].
 *      Any value < 1 or > n is irrelevant: leave it where it is and move on.
 *   2. Walk i from 0. If nums[i] is in range and not already sitting at index
 *      nums[i] - 1, swap it there and re-examine the value that just arrived at i
 *      (do NOT advance i). Otherwise advance i.
 *   3. Skip the swap when nums[correctIdx] == nums[i]: that means either the value
 *      is already home, or a duplicate already occupies its home. Without this
 *      guard two equal values would swap with each other forever.
 *   4. Scan again: the first index j with nums[j] != j + 1 gives answer j + 1.
 *      If every slot matches, answer n + 1.
 *
 * KEY INSIGHT
 *   The array can be used as its own hash set: value v belongs at index v - 1.
 *   Each swap parks one value at its final home, so there are at most n swaps in
 *   total even though i sometimes stays put, which is why the nested-looking loop
 *   is O(n). Pattern to recognise: "values in [1, n], O(1) space" -> cyclic sort.
 *
 * COMPLEXITY
 *   Time  O(n)  at most n swaps plus two linear scans
 *   Space O(1)  in place; the input order is destroyed, which is the price paid
 *
 * INTERVIEW FOLLOW-UPS
 *   - Alternative in-place marking: clamp out-of-range to n + 1, then negate index
 *     |v| - 1 as a "seen" flag; first positive slot gives the answer.
 *   - What if the input must not be modified? Then O(n) space (HashSet) is needed.
 *   - Why is the answer bounded by n + 1? (pigeonhole: n slots, n distinct values max)
 *   - Same loop solves Find All Duplicates / Find All Missing / Find Corrupt Pair.
 *
 * RUN
 *   main() runs 6 cases (typical, packed, all out of range, two duplicate shapes,
 *   empty) and prints actual vs expected.
 */

import java.util.Arrays;

class FirstMissingPositive {

    /** Returns the smallest positive integer absent from nums; reorders nums in place. */
    public int firstMissingPositive(int[] nums) {
        int n = nums.length;

        // Phase 1: send every value in [1, n] to index value - 1.
        int i = 0;
        while (i < n) {
            if (nums[i] < 1 || nums[i] > n) {
                i++;               // out of range: cannot be the answer, ignore it
                continue;
            }
            int correctIdx = nums[i] - 1;
            if (nums[correctIdx] != nums[i]) {
                swap(nums, i, correctIdx);   // i is not advanced: re-check the new arrival
            } else {
                i++;               // already home, or a duplicate holds the slot
            }
        }

        // Phase 2: the first index that breaks the 1, 2, 3, ... pattern.
        for (int j = 0; j < n; j++) {
            if (nums[j] != j + 1) {
                return j + 1;
            }
        }
        return n + 1;   // 1..n all present
    }

    private static void swap(int[] nums, int a, int b) {
        int temp = nums[a];
        nums[a] = nums[b];
        nums[b] = temp;
    }

    private static void print(String label, int[] nums, int expected) {
        String before = Arrays.toString(nums);   // capture before in-place mutation
        int actual = new FirstMissingPositive().firstMissingPositive(nums);
        System.out.println(label + ": " + before + " -> " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 typical         ", new int[]{3, 4, -1, 1}, 2);
        print("case 2 fully packed    ", new int[]{1, 2, 3, 4, 5}, 6);
        print("case 3 all out of range", new int[]{7, 8, 9, 11, 12}, 1);
        print("case 4 duplicate ones  ", new int[]{1, 1}, 2);
        print("case 5 duplicate twos  ", new int[]{2, 2}, 1);
        print("case 6 empty           ", new int[]{}, 1);
    }
}
