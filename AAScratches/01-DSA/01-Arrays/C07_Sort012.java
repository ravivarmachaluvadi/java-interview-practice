/*
 * =====================================================================
 *  Sort Colors (Sort 0s, 1s, 2s)              LeetCode 75 | Medium   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   nums holds only the values 0, 1 and 2. Sort it in place, in one pass, without a
 *   library sort and without counting first. Output is the same array, sorted.
 *
 * EXAMPLE
 *   [2, 0, 2, 1, 1, 0]  ->  [0, 0, 1, 1, 2, 2]
 *   [2, 2, 1, 0, 0]     ->  [0, 0, 1, 2, 2]     every 2 lands via the high pointer
 *   [1, 1, 1, 1]        ->  [1, 1, 1, 1]        only mid moves
 *   []                  ->  []                  loop never runs
 *
 * APPROACH  (Dutch National Flag, three-way partition)
 *   1. Three pointers split the array into four regions:
 *        [0, low-1] = 0s   [low, mid-1] = 1s   [mid, high] = unknown   [high+1, n-1] = 2s
 *   2. Look at nums[mid]:
 *        0 -> swap with nums[low]; low++ and mid++ (the swapped-in value is a known 1)
 *        1 -> mid++ (already in the right region)
 *        2 -> swap with nums[high]; high-- only. mid stays put.
 *   3. Stop when mid passes high: the unknown region is empty, so the array is sorted.
 *
 * KEY INSIGHT
 *   mid does NOT advance after swapping in a 2, because the value that came from
 *   nums[high] has never been examined. After swapping in a 0 it is safe to advance,
 *   because everything between low and mid is a 1 that was already classified.
 *   Pattern: any "partition into three buckets in place" question (pivot partition in
 *   quicksort, negatives/zeros/positives, less/equal/greater than k) is this loop.
 *
 * COMPLEXITY
 *   Time  O(n)  every iteration either advances mid or retreats high
 *   Space O(1)  three ints, swaps in place
 *
 * INTERVIEW FOLLOW-UPS
 *   - Why not just count 0s/1s/2s and overwrite? Works (two passes) but is not one pass.
 *   - Why does mid not move on a 2 but does move on a 0? (see KEY INSIGHT)
 *   - Generalise to k colours: a single pass no longer works; use counting or sort.
 *   - Same partition as quicksort's three-way partition (handles duplicate keys well).
 *
 * RUN
 *   main() runs 5 cases (typical, all-2s-first, all equal, single, empty) and prints
 *   actual vs expected.
 */

import java.util.Arrays;

class Sort012 {

    /** Sorts nums in place so all 0s come first, then all 1s, then all 2s. */
    public void sortColors(int[] nums) {
        int low = 0;               // next slot for a 0
        int mid = 0;               // current element being classified
        int high = nums.length - 1; // next slot for a 2

        while (mid <= high) {
            switch (nums[mid]) {
                case 0 -> {
                    swap(nums, low, mid);
                    low++;
                    mid++; // the value swapped in from low was already classified as 1
                }
                case 1 -> mid++;
                case 2 -> {
                    swap(nums, mid, high);
                    high--; // mid stays: the value swapped in from high is unexamined
                }
                default -> throw new IllegalArgumentException(
                        "Unexpected value: " + nums[mid] + " (expected 0, 1, or 2)");
            }
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    private static void run(String label, int[] nums, String expected) {
        new Sort012().sortColors(nums);
        System.out.println(label + ": " + Arrays.toString(nums) + "   expected " + expected);
    }

    public static void main(String[] args) {
        run("case 1 typical      ", new int[]{2, 0, 2, 1, 1, 0}, "[0, 0, 1, 1, 2, 2]");
        run("case 2 twos first   ", new int[]{2, 2, 1, 0, 0}, "[0, 0, 1, 2, 2]");
        run("case 3 all equal    ", new int[]{1, 1, 1, 1}, "[1, 1, 1, 1]");
        run("case 4 single       ", new int[]{0}, "[0]");
        run("case 5 empty        ", new int[]{}, "[]");
    }
}
