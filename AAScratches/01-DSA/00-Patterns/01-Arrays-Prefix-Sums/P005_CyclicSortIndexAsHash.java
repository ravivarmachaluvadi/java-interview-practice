/*
 * =====================================================================
 *  P005 Cyclic Sort: Index as Hash   Canonical LC 448 | Easy   MUST-KNOW
 * =====================================================================
 *
 * PROBLEM  (canonical: LeetCode 448, Find All Numbers Disappeared in an Array)
 *   nums has n integers, each in 1..n; some appear twice and some not at all. Return every
 *   number in 1..n that does not appear, in O(n) time and O(1) extra space.
 *
 * EXAMPLE
 *   [4, 3, 2, 7, 8, 2, 3, 1]  ->  [5, 6]
 *   [1, 1]                    ->  [2]
 *
 * RECOGNIZE WHEN
 *   - Values are bounded by the length: "1..n", "0..n", "first missing positive".
 *   - The ask is missing / duplicate / misplaced numbers with O(1) extra space.
 *   - A HashSet would solve it, but the interviewer bans extra memory.
 *   Not this if: you may not modify the array -> P031_FastSlowPointers (LC 287) or XOR /
 *   sum tricks (P099_XorTricks); values are unbounded -> P022_HashSetRunDetection.
 *
 * TEMPLATE
 *   i = 0
 *   while i < n:
 *       j = nums[i] - 1                        // the slot this value belongs in
 *       if 0 <= j < n and nums[i] != nums[j]:  swap(i, j)   // do NOT advance i
 *       else:                                  i++
 *   for i in 0..n-1: if nums[i] != i + 1 -> i + 1 is missing, nums[i] is extra
 *
 * APPROACH
 *   1. Put every value v in slot v - 1 by swapping; each swap fixes one value for good.
 *   2. Scan: a slot whose value is wrong means its own number (i + 1) never came.
 *
 * KEY INSIGHT
 *   When values live in 1..n the array can be its own hash table: index v - 1 is the
 *   bucket for v. Each swap puts at least one value home, so there are at most n swaps.
 *
 * COMPLEXITY
 *   Time O(n) (at most n swaps plus n increments), space O(1) besides the output.
 *
 * VARIATIONS  (same template; what changes)
 *   [coded] LC 442  Find All Duplicates      after sorting, the value in a wrong slot is
 *                                            a duplicate
 *   [coded] LC 645  Set Mismatch             the one wrong slot gives both answers
 *   [coded] LC 41   First Missing Positive   ignore values outside 1..n; first wrong slot
 *           LC 268  Missing Number           range 0..n: slot v; or P099_XorTricks
 *           LC 287  Find the Duplicate       read-only -> P031_FastSlowPointers
 *           Sign marking                     flip nums[|v| - 1] negative instead of swapping;
 *                                            positives left = missing numbers
 *
 * PITFALLS
 *   - Advancing i after a swap skips the value that just arrived at i.
 *   - The guard is nums[i] != nums[j], not i != j; otherwise duplicates swap forever.
 *   - LC 41: values can be 0, negative or > n; leave them where they are.
 *
 * DEEP DIVE
 *   B10_FindAllMissedNumbers, C14_FindAllDuplicatesInAnArray, B11_FindCorruptPair,
 *   D01_FirstMissingPositive (all in 01-Arrays)
 *
 * RUN
 *   main() prints every case as "label: actual   expected value".
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class CyclicSortIndexAsHash {

    // The shared step: move every value v in 1..n to index v - 1.
    static void cyclicSort(int[] nums) {
        int i = 0;
        while (i < nums.length) {
            int j = nums[i] - 1;
            if (j >= 0 && j < nums.length && nums[i] != nums[j]) {
                int t = nums[i];
                nums[i] = nums[j];
                nums[j] = t;
            } else {
                i++;
            }
        }
    }

    // Canonical LC 448.
    static List<Integer> findDisappearedNumbers(int[] nums) {
        cyclicSort(nums);
        List<Integer> missing = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != i + 1) {
                missing.add(i + 1);
            }
        }
        return missing;
    }

    // LC 442: each value appears once or twice; the extra copies end up in wrong slots.
    static List<Integer> findDuplicates(int[] nums) {
        cyclicSort(nums);
        List<Integer> dups = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != i + 1) {
                dups.add(nums[i]);
            }
        }
        Collections.sort(dups);               // any order is accepted; sorted for printing
        return dups;
    }

    // LC 645: one number is doubled and one is missing -> [duplicate, missing].
    static int[] findErrorNums(int[] nums) {
        cyclicSort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != i + 1) {
                return new int[]{nums[i], i + 1};
            }
        }
        return new int[0];
    }

    // LC 41: only 1..n can be placed; the first slot that is wrong is the answer.
    static int firstMissingPositive(int[] nums) {
        cyclicSort(nums);
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != i + 1) {
                return i + 1;
            }
        }
        return nums.length + 1;
    }

    private static void check(String label, Object actual, Object expected) {
        System.out.println(label + ": " + actual + "   expected " + expected);
    }

    public static void main(String[] args) {
        check("LC 448 [4,3,2,7,8,2,3,1]",
                findDisappearedNumbers(new int[]{4, 3, 2, 7, 8, 2, 3, 1}), "[5, 6]");
        check("LC 448 [1,1]", findDisappearedNumbers(new int[]{1, 1}), "[2]");
        check("LC 448 [1,2,3] none missing", findDisappearedNumbers(new int[]{1, 2, 3}), "[]");

        check("LC 442 [4,3,2,7,8,2,3,1]",
                findDuplicates(new int[]{4, 3, 2, 7, 8, 2, 3, 1}), "[2, 3]");
        check("LC 442 [1,1,2]", findDuplicates(new int[]{1, 1, 2}), "[1]");
        check("LC 442 [1] none", findDuplicates(new int[]{1}), "[]");

        check("LC 645 [1,2,2,4]", Arrays.toString(findErrorNums(new int[]{1, 2, 2, 4})), "[2, 3]");
        check("LC 645 [1,1]", Arrays.toString(findErrorNums(new int[]{1, 1})), "[1, 2]");
        check("LC 645 [3,2,3,4,6,5]",
                Arrays.toString(findErrorNums(new int[]{3, 2, 3, 4, 6, 5})), "[3, 1]");

        check("LC 41 [1,2,0]", firstMissingPositive(new int[]{1, 2, 0}), 3);
        check("LC 41 [3,4,-1,1]", firstMissingPositive(new int[]{3, 4, -1, 1}), 2);
        check("LC 41 [7,8,9,11,12] none placeable",
                firstMissingPositive(new int[]{7, 8, 9, 11, 12}), 1);
        check("LC 41 [1,1] duplicate", firstMissingPositive(new int[]{1, 1}), 2);
    }
}
