/*
 * =====================================================================
 *  Set Difference of Two Arrays (symmetric difference)      Related: LeetCode 2215 | Easy
 * =====================================================================
 *
 * PROBLEM
 *   Given two integer arrays, return a sorted list of every distinct value that appears in
 *   exactly one of them: (nums1 union nums2) minus (nums1 intersect nums2). Values that repeat
 *   inside one array count once. Arrays may be empty.
 *
 * EXAMPLE
 *   nums1 = [1, 2, 3],    nums2 = [2, 4, 6]  ->  [1, 3, 4, 6]   2 is in both, so it is dropped
 *   nums1 = [5, 1, 3, 3], nums2 = [3, 9, 5]  ->  [1, 9]         the repeated 3 is still "in both"
 *   nums1 = [],           nums2 = [7, 7]     ->  [7]            empty side, duplicate deduped
 *   nums1 = [4, 4],       nums2 = [4]        ->  []             everything cancels
 *
 * APPROACH  (two HashSets, then sort)  -- setDifferenceWithHashSets
 *   1. Pour each array into its own HashSet; this dedupes within an array for free.
 *   2. Every element of set1 missing from set2 goes to the result; then the mirror scan.
 *   3. HashSet order is arbitrary, so sort the result list once at the end.
 *
 * APPROACH  (sort + two pointers)  -- setDifferenceWithTwoPointers
 *   1. Sort both arrays. Walk them with pointers i and j.
 *   2. Equal heads: the value is common, skip it on both sides.
 *      Smaller head: it has no partner, emit it and advance that side only.
 *   3. After the walk, emit the leftovers of whichever array remains.
 *   4. Each time a value is consumed, also skip its in-array repeats so it is counted once.
 *   Fixed: the original skipped no in-array repeats, so [5,1,3,3] vs [3,9,5] gave [1,3,9].
 *
 * KEY INSIGHT
 *   "In exactly one array" is two one-sided differences glued together (A\B then B\A).
 *   A Set answers "is x in the other array?" in O(1); the two-pointer walk answers the same
 *   question from sorted order alone, with no hashing - it still copies both inputs here so
 *   the caller's arrays are left untouched.
 *
 * COMPLEXITY
 *   HashSets:     Time O(n + m + k log k)  build, scan, sort the k-element result;  Space O(n + m)
 *   Two pointers: Time O(n log n + m log m) sorting dominates;  Space O(n + m) both inputs cloned
 *                 Space drops to O(log n) stack if you may sort the caller's arrays in place.
 *
 * INTERVIEW FOLLOW-UPS
 *   - Return the two one-sided lists separately: see B01_FindTheDifferenceOfTwoArrays.
 *   - Inputs already sorted and mutable: drop the clones and two pointers needs no extra memory.
 *   - Streams of unknown length: HashSet version works online, two pointers does not.
 *   - Keep multiplicity (multiset difference): switch the sets for count maps.
 *
 * RUN
 *   main() runs 4 cases through both methods and prints actual vs expected.
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class SetDifferenceOfTwoArrays {

    /** Approach 1: two HashSets, one-sided scan each way, sort at the end. */
    public static List<Integer> setDifferenceWithHashSets(int[] nums1, int[] nums2) {
        Set<Integer> set1 = new HashSet<>();
        Set<Integer> set2 = new HashSet<>();
        for (int num : nums1) set1.add(num);
        for (int num : nums2) set2.add(num);

        List<Integer> result = new ArrayList<>();
        for (int num : set1) {
            if (!set2.contains(num)) result.add(num);
        }
        for (int num : set2) {
            if (!set1.contains(num)) result.add(num);
        }
        Collections.sort(result);   // HashSet iteration order is arbitrary
        return result;
    }

    /** Approach 2: sort both arrays and merge-walk them, skipping values present on both sides. */
    public static List<Integer> setDifferenceWithTwoPointers(int[] nums1, int[] nums2) {
        int[] a = nums1.clone();    // sort copies so the caller's arrays are untouched
        int[] b = nums2.clone();
        Arrays.sort(a);
        Arrays.sort(b);

        List<Integer> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < a.length && j < b.length) {
            if (a[i] == b[j]) {
                int common = a[i];                 // in both arrays: drop it from both sides
                i = skipRunOf(a, i, common);
                j = skipRunOf(b, j, common);
            } else if (a[i] < b[j]) {
                result.add(a[i]);                  // only in a
                i = skipRunOf(a, i, a[i]);
            } else {
                result.add(b[j]);                  // only in b
                j = skipRunOf(b, j, b[j]);
            }
        }
        // Leftovers on one side cannot have a partner; emit each distinct value once.
        while (i < a.length) { result.add(a[i]); i = skipRunOf(a, i, a[i]); }
        while (j < b.length) { result.add(b[j]); j = skipRunOf(b, j, b[j]); }
        return result;
    }

    /** Returns the first index at or after {@code from} whose value differs from {@code value}. */
    private static int skipRunOf(int[] sorted, int from, int value) {
        while (from < sorted.length && sorted[from] == value) from++;
        return from;
    }

    private static void print(String label, int[] nums1, int[] nums2, String expected) {
        System.out.println(label + " hashSets    : " + setDifferenceWithHashSets(nums1, nums2)
                + "   expected " + expected);
        System.out.println(label + " twoPointers : " + setDifferenceWithTwoPointers(nums1, nums2)
                + "   expected " + expected);
    }

    public static void main(String[] args) {
        print("case 1 (typical)  ", new int[]{1, 2, 3}, new int[]{2, 4, 6}, "[1, 3, 4, 6]");
        print("case 2 (dup in a) ", new int[]{5, 1, 3, 3}, new int[]{3, 9, 5}, "[1, 9]");
        print("case 3 (empty a)  ", new int[]{}, new int[]{7, 7}, "[7]");
        print("case 4 (all same) ", new int[]{4, 4}, new int[]{4}, "[]");
    }
}
