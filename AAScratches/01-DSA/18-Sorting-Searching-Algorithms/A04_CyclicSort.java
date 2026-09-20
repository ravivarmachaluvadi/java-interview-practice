/*
 * =====================================================================
 *  Cyclic Sort                                Classic | Easy  MUST-KNOW
 * =====================================================================
 *
 * PROBLEM
 *   Sort an array that holds a permutation of 1..n, where n is the array length,
 *   in O(n) time and O(1) space. Because every value has exactly one legal slot,
 *   no comparisons between elements are needed - only addressing.
 *
 * EXAMPLE
 *   [3, 1, 5, 4, 2]  ->  [1, 2, 3, 4, 5]
 *   [1]              ->  [1]              (single element, already home)
 *   [1, 2, 3, 4]     ->  [1, 2, 3, 4]     (sorted input, n swaps-free passes)
 *
 * APPROACH  (value-to-index placement)
 *   1. Keep a cursor i at 0. Value v belongs at index v - 1, call it correctIndex.
 *   2. If the value already sitting at correctIndex equals nums[i], index i is
 *      settled - advance i.
 *   3. Otherwise swap nums[i] with nums[correctIndex]. The swapped-in value is
 *      now at least one element closer to home, so do NOT advance i; re-test it.
 *   4. Stop when i reaches n. Every element is then at index value - 1.
 *
 * KEY INSIGHT
 *   Each swap puts at least one value permanently in its final slot, so there
 *   are at most n swaps across the whole run even though the loop can revisit i.
 *   That is why the while loop is O(n) and not O(n^2). Recognise the pattern by
 *   its precondition: "values are 1..n" or "0..n" with no other structure - the
 *   moment you see it, the missing-number / duplicate / first-missing-positive
 *   family is the same loop with one extra scan at the end.
 *   Compare by VALUE (nums[i] != nums[correctIndex]), not by index. With
 *   duplicates, an index comparison loops forever; the value comparison exits.
 *
 * COMPLEXITY
 *   Time  O(n)   at most n swaps plus at most n cursor advances.
 *   Space O(1)   in place, one temp variable.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Range 0..n instead of 1..n: correctIndex becomes nums[i], not nums[i] - 1.
 *   - Missing Number (LeetCode 268) / Find All Numbers Disappeared (448): cyclic
 *     sort, then report the first index where nums[i] != i + 1.
 *   - Find the Duplicate Number (287): the value that will not settle is the dup.
 *   - First Missing Positive (41, hard): same loop but skip values outside 1..n,
 *     which is the only extra guard needed.
 *
 * RUN
 *   main() runs 3 cases (typical shuffle, single element, already sorted) and
 *   prints actual vs expected.
 */
import java.util.Arrays;

class CyclicSort {

    /** Sorts a permutation of 1..n in place. Values outside that range are not supported. */
    public static void cyclicSort(int[] nums) {
        int i = 0;
        while (i < nums.length) {
            int correctIndex = nums[i] - 1; // value v belongs at index v - 1
            if (nums[i] != nums[correctIndex]) {
                swap(nums, i, correctIndex);
                // Deliberately do not advance i: re-test whatever landed here.
            } else {
                i++; // this slot is final
            }
        }
    }

    private static void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    private static void print(String label, int[] actual, int[] expected) {
        System.out.println(label + ": " + Arrays.toString(actual)
                + "   expected " + Arrays.toString(expected));
    }

    public static void main(String[] args) {
        int[] typical = {3, 1, 5, 4, 2};
        cyclicSort(typical);
        print("case 1 typical      ", typical, new int[]{1, 2, 3, 4, 5});

        int[] single = {1};
        cyclicSort(single);
        print("case 2 single       ", single, new int[]{1});

        int[] alreadySorted = {1, 2, 3, 4};
        cyclicSort(alreadySorted);
        print("case 3 sorted input ", alreadySorted, new int[]{1, 2, 3, 4});
    }
}
